package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MiniBrass runner is responsible for executing branch-and-bound or other
 * searches that involve executing several MiniZinc processes
 * <p>
 * usage: minibrass constraintModel.mzn file.mbr [dataFiles.dzn]
 *
 * @author alexander
 */
public class MiniBrassRunner {
	private static final int RANDOM_SEED_LIMIT = Integer.MAX_VALUE / 2;
	private final MiniZincRunner miniZincRunner = new MiniZincRunner();
	private final MiniBrassCompiler miniBrassCompiler = new MiniBrassCompiler();
	private boolean writeIntermediateFiles = true;
	private boolean debug = false;
	private int initialRandomSeed = 1337;
	private boolean randomize = false;
	private Random randomSequence;
	private Integer timeoutInSeconds = null; // in milliseconds


	private File originalMiniZincFile;
	private File workingMiniZincFile;
	private String lastSolvableMiniZincModel;
	private String workingMiniZincModel;
	private List<MiniZincSolution> allSolutions;

	public MiniBrassRunner() {
		this(new MiniZincConfiguration());
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		setMiniZincConfiguration(configuration);
	}

	private void initializeBranchAndBound(File miniZincFile) throws IOException {
		// init fields
		originalMiniZincFile = miniZincFile;
		workingMiniZincFile = miniZincFile;
		allSolutions = new LinkedList<>();
		if (randomize) {
			randomSequence = new Random(initialRandomSeed);
		}
		miniBrassCompiler.setMinizincOnly(true);
		workingMiniZincModel = FileUtils.readFileToString(miniZincFile, StandardCharsets.UTF_8);
	}

	public Collection<MiniZincSolution> executeBranchAndBoundWithParetoOptima(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws MiniBrassParseException, IOException {
		// find single optimal solution
		executeBranchAndBound(miniZincFile, miniBrassFile, dataFiles);

		// find the other solutions that are equally good
		replaceMiniZincCode(lastSolvableMiniZincModel);
		miniZincRunner.getConfiguration().setUseAllSolutions(true);
		MiniZincResult result = runMiniZinc(workingMiniZincFile, dataFiles);
		miniZincRunner.getConfiguration().setUseAllSolutions(false);
		cleanup(workingMiniZincFile);

		// interpret result and return solutions
		return result.isSolvedAndValid() ? result.getSolutions() : Collections.emptySet();
	}

	public MiniZincSolution executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws IOException, MiniBrassParseException {
		// initialize
		initializeBranchAndBound(miniZincFile);
		MiniZincSolution solution;
		MiniZincSolution lastSolution = null;
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();

		// parse and compile MiniBrass
		String compiledMiniBrassCode = miniBrassCompiler.compileInMemory(miniBrassFile);
		// for domination search
		String getBetterConstraint = miniBrassCompiler.getUnderlyingParser().getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();

		// search for solutions
		appendMiniZincCode(compiledMiniBrassCode, true);
		while ((solution = findNextSolution(workingMiniZincFile, dataFiles)) != null) {
			lastSolvableMiniZincModel = workingMiniZincModel;

			// append solution
			allSolutions.add(solution);
			lastSolution = solution;

			// print solution in debug mode
			String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, solution) + ";";

			if (debug) {
				System.out.println("Found solution: ");
				System.out.println(solution.getRawDznSolution());

				// process getBetterConstraint with actual solution
				System.out.println("I got the following template constraint: ");
				System.out.println(getBetterConstraint);
				System.out.println(updatedConstraint);
			}

			// add constraint to model and solve again
			appendMiniZincCode(updatedConstraint);
		}
		cleanup(workingMiniZincFile);
		return lastSolution;
	}

	private void appendMiniZincCode(String additionalCode) throws IOException {
		appendMiniZincCode(additionalCode, false);
	}

	private void appendMiniZincCode(String additionalCode, boolean enforceIntermediateFile) throws IOException {
		// update in-memory model
		additionalCode = "\n" + additionalCode;
		workingMiniZincModel += additionalCode;

		if (writeIntermediateFiles || enforceIntermediateFile) {
			migrateToNewWorkingMiniZincFile();
		}
		try (FileWriter fw = new FileWriter(workingMiniZincFile, true)) {
			fw.write(additionalCode);
		}
	}

	private void replaceMiniZincCode(String newCode) throws IOException {
		workingMiniZincModel = newCode;

		if (writeIntermediateFiles) {
			workingMiniZincFile = getNextMiniZincFile(workingMiniZincFile);
		}
		FileUtils.writeStringToFile(workingMiniZincFile, workingMiniZincModel, StandardCharsets.UTF_8);
	}

	private void migrateToNewWorkingMiniZincFile() throws IOException {
		migrateToNewWorkingMiniZincFile(workingMiniZincFile);
	}

	private void migrateToNewWorkingMiniZincFile(File currentFile) throws IOException {
		workingMiniZincFile = getNextMiniZincFile(currentFile);
		FileUtils.copyFile(currentFile, workingMiniZincFile);
		cleanup(currentFile);
	}

	private File getNextMiniZincFile(File miniZincFile) {
		String name = FilenameUtils.removeExtension(miniZincFile.getName());
		int modelIndex = 0;
		Matcher modelIndexMatcher = Pattern.compile(".*_([0-9]+)$").matcher(name);
		if (modelIndexMatcher.matches()) {
			String modelIndexText = modelIndexMatcher.group(1);
			name = name.substring(0, name.length() - modelIndexText.length() - 1);
			modelIndex = Integer.parseInt(modelIndexText) + 1;
		}
		String nextName = FilenameUtils.removeExtension(name) + "_" + modelIndex + ".mzn";
		return new File(miniZincFile.getParentFile(), nextName);
	}

	private void cleanup(File miniZincFile) {
		if (!miniZincFile.equals(originalMiniZincFile) && !debug)
			FileUtils.deleteQuietly(miniZincFile);
	}

	private MiniZincSolution findNextSolution(File miniZincFile, List<File> dataFiles) {
		MiniZincResult result = runMiniZinc(miniZincFile, dataFiles);
		return result.isSolvedAndValid() ? result.getLastSolution() : null;
	}

	private MiniZincResult runMiniZinc(File miniZincFile, List<File> dataFiles) {
		if (randomize) {
			miniZincRunner.getConfiguration().setRandomSeed(randomSequence.nextInt(RANDOM_SEED_LIMIT));
		}
		return miniZincRunner.solve(miniZincFile, dataFiles, timeoutInSeconds);
	}

	public MiniZincConfiguration getMiniZincRunnerConfiguration() {
		return miniZincRunner.getConfiguration();
	}

	public void setMiniZincConfiguration(MiniZincConfiguration configuration) {
		configuration.setUseAllSolutions(false);
		miniZincRunner.setConfiguration(configuration);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		this.miniZincRunner.setDebug(debug);
	}

	public List<MiniZincSolution> getAllSolutions() {
		return allSolutions;
	}

	public void setAllSolutions(List<MiniZincSolution> allSolutions) {
		this.allSolutions = allSolutions;
	}

	public MiniZincRunner getMiniZincRunner() {
		return miniZincRunner;
	}

	public int getInitialRandomSeed() {
		return initialRandomSeed;
	}

	public void setInitialRandomSeed(int initialRandomSeed) {
		this.initialRandomSeed = initialRandomSeed;
		this.randomize = true;
	}

	public boolean writeIntermediateFiles() {
		return writeIntermediateFiles;
	}

	public void setWriteIntermediateFiles(boolean writeIntermediateFiles) {
		this.writeIntermediateFiles = writeIntermediateFiles;
	}

	public void setTimeoutInSeconds(Integer timeout) {
		this.timeoutInSeconds = timeout;
	}

}

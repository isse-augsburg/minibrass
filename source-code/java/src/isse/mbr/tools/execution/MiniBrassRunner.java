package isse.mbr.tools.execution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;

/**
 * The MiniBrass runner is responsible for executing branch-and-bound or other
 * searches that involve executing several MiniZinc processes
 * 
 * usage: minibrass constraintModel.mzn file.mbr [dataFiles.dzn]
 * 
 * @author alexander
 *
 */
public class MiniBrassRunner {
	private MiniZincRunner miniZincRunner;
	private MiniBrassCompiler miniBrassCompiler;
	private boolean writeIntermediateFiles;
	private boolean debug;
	private int modelIndex;
	private File originalMiniZincFile;
	private List<MiniZincSolution> allSolutions;
	
	public MiniBrassRunner() {
		miniZincRunner = new MiniZincRunner();
		MiniZincConfiguration config = new MiniZincConfiguration();
		config.setUseAllSolutions(true);

		miniZincRunner.setConfiguration(config);
		miniBrassCompiler = new MiniBrassCompiler();
		writeIntermediateFiles = true;
		debug = false;
		modelIndex = 0;
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		this();
		miniZincRunner.setConfiguration(configuration);
	}

	public MiniZincSolution executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws IOException, MiniBrassParseException {
		MiniZincSolution solution;
		MiniZincSolution lastSolution = null;
		
		allSolutions = new LinkedList<>();
		miniBrassCompiler.setMinizincOnly(true);
		String compiledMiniBrassCode = miniBrassCompiler.compileInMemory(miniBrassFile);
		originalMiniZincFile = miniZincFile;
		MiniBrassParser parser = miniBrassCompiler.getUnderlyingParser();
		String getBetterConstraint = parser.getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();

		File workingMiniZincModel = appendMiniZincCode(miniZincFile, compiledMiniBrassCode);

		while ((solution = hasNextSolution(workingMiniZincModel, dataFiles)) != null) {
			// append solution
			allSolutions.add(solution);
			lastSolution = solution;
			
			// print solution
			System.out.println("Found solution: ");
			System.out.println(solution.getRawDznSolution());
			// process getBetterConstraint with actual solution
			System.out.println("I got the following template constraint: ");
			System.out.println(getBetterConstraint);
			String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, solution)
					+ ";";
			System.out.println(updatedConstraint);

			// add constraint to model
			// TODO this is not the best way to do it - we should keep the String in main
			// memory
			workingMiniZincModel = appendMiniZincCode(workingMiniZincModel, updatedConstraint);
			// solve again
		}
		cleanup(workingMiniZincModel);
		return lastSolution;
	}

	private File appendMiniZincCode(File miniZincFile, String updatedConstraint) throws IOException {
		if (writeIntermediateFiles) {
			String name = miniZincFile.getName();
			String nextName = FilenameUtils.removeExtension(name) + "_" + (modelIndex++) + ".mzn";
			File nextFile = new File(miniZincFile.getParentFile(), nextName);
			FileUtils.copyFile(miniZincFile, nextFile);
			cleanup(miniZincFile);
			miniZincFile = nextFile;

		}
		FileWriter fw = new FileWriter(miniZincFile, true);
		fw.write("\n");
		fw.write(updatedConstraint);
		fw.close();
		return miniZincFile;
	}

	private void cleanup(File miniZincFile) {
		if (!miniZincFile.equals(originalMiniZincFile) && !debug)
			FileUtils.deleteQuietly(miniZincFile);
	}

	private MiniZincSolution hasNextSolution(File miniZincFile, List<File> dataFiles) {
		MiniZincResult result = miniZincRunner.solve(miniZincFile, dataFiles, -1);
		return !result.isInvalidated() && result.isSolved() ? result.getLastSolution() : null;
	}

	public MiniZincConfiguration getMiniZincRunnerConfiguration() {
		return miniZincRunner.getConfiguration();
	}

	public void setMiniZincConfiguration(MiniZincConfiguration configuration) {
		miniZincRunner.setConfiguration(configuration);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public List<MiniZincSolution> getAllSolutions() {
		return allSolutions;
	}

	public void setAllSolutions(List<MiniZincSolution> allSolutions) {
		this.allSolutions = allSolutions;
	}

}

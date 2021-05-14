package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	private boolean debug = false;
	private int initialRandomSeed = 1337;
	private boolean randomize = false;
	private Random randomSequence;
	private Integer timeoutInSeconds = null; // in milliseconds


	private WorkingModelManager workingModelManager;
	private String lastSolvableMiniZincModel;
	private List<MiniZincSolution> allSolutions;

	public MiniBrassRunner() {
		this(new MiniZincConfiguration());
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		setMiniZincConfiguration(configuration);
	}

	private void initializeBranchAndBound(File miniZincFile) throws IOException {
		// init fields
		workingModelManager = WorkingModelManager.create(miniZincFile, isDebug(), isDebug());
		allSolutions = new LinkedList<>();
		if (randomize) {
			randomSequence = new Random(initialRandomSeed);
		}
		miniBrassCompiler.setMinizincOnly(true);
	}

	public Collection<MiniZincSolution> executeBranchAndBoundWithParetoOptima(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws MiniBrassParseException, IOException {
		// find single optimal solution
		executeBranchAndBound(miniZincFile, miniBrassFile, dataFiles);

		// find the other solutions that are equally good
		workingModelManager.replaceModel(lastSolvableMiniZincModel);
		miniZincRunner.getConfiguration().setUseAllSolutions(true);
		MiniZincResult result = runMiniZinc(workingModelManager.getFile(), dataFiles);
		miniZincRunner.getConfiguration().setUseAllSolutions(false);
		workingModelManager.cleanup();

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
		workingModelManager.appendToModel(compiledMiniBrassCode);
		while ((solution = findNextSolution(workingModelManager.getFile(), dataFiles)) != null) {
			lastSolvableMiniZincModel = workingModelManager.getModel();

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
			workingModelManager.appendToModel(updatedConstraint);
		}
		workingModelManager.cleanup();
		return lastSolution;
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

	public void setTimeoutInSeconds(Integer timeout) {
		this.timeoutInSeconds = timeout;
	}

}

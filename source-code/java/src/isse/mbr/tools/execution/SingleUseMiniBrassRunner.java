package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.lang3.stream.Streams;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The SingleRunMiniBrass runner is responsible for executing branch-and-bound or other
 * searches that involve executing several MiniZinc processes.
 *
 * Instances of this class only are run once and are meant for internal use.
 * Use the {@link MiniBrassRunner} for a more flexible usage.
 *
 * @see MiniBrassRunner
 * @author alexander
 * @author elias
 */
public class SingleUseMiniBrassRunner {
	private static final int RANDOM_SEED_LIMIT = Integer.MAX_VALUE / 2;
	private final MiniZincRunner miniZincRunner;
	private final MiniBrassCompiler miniBrassCompiler;
	private final boolean debug;
	private final Random randomSequence;
	private final Integer timeoutInSeconds;
	private final MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();
	private final Set<MiniZincSolution> allSolutions = new HashSet<>();
	private final File miniZincFile;
	private final File miniBrassFile;
	private final List<File> dataFiles;
	private Set<MiniZincSolution> finalSolutions;
	private WorkingModelManager workingModelManager;
	private String getBetterConstraint;

	private SingleUseMiniBrassRunner(MiniZincRunner miniZincRunner, MiniBrassCompiler miniBrassCompiler, File miniZincFile,
	                                 File miniBrassFile, List<File> dataFiles, boolean debug, Integer randomSeed, Integer timeoutInSeconds) {
		this.miniZincRunner = miniZincRunner;
		this.miniBrassCompiler = miniBrassCompiler;
		this.miniZincFile = miniZincFile;
		this.miniBrassFile = miniBrassFile;
		this.dataFiles = dataFiles;
		this.randomSequence = randomSeed != null ? new Random(randomSeed) : null;
		this.debug = debug;
		this.timeoutInSeconds = timeoutInSeconds;

		miniBrassCompiler.setMinizincOnly(true);
	}

	public static SingleUseMiniBrassRunner executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles,
	                                                             MiniZincRunner miniZincRunner, MiniBrassCompiler miniBrassCompiler,
	                                                             boolean debug, Integer randomSeed, Integer timeoutInSeconds)
			throws MiniBrassParseException, IOException {
		SingleUseMiniBrassRunner runner = new SingleUseMiniBrassRunner(miniZincRunner, miniBrassCompiler, miniZincFile, miniBrassFile,
				dataFiles, debug, randomSeed, timeoutInSeconds);
		runner.executeBranchAndBound();
		return runner;
	}

	public void executeBranchAndBound()
			throws MiniBrassParseException, IOException {
		// set up working file and model
		workingModelManager = WorkingModelManager.create(miniZincFile, debug, debug);

		// parse and compile MiniBrass
		String compiledMiniBrassCode = miniBrassCompiler.compileInMemory(miniBrassFile);
		getBetterConstraint = miniBrassCompiler.getUnderlyingParser().getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();
		workingModelManager.appendToModel(compiledMiniBrassCode);

		// search for solutions
		MiniZincResult initialResult = runMiniZinc(workingModelManager.getFile(), dataFiles);
		if (initialResult.isSolvedAndValid()) {
			String model = workingModelManager.getModel(); // do not inline – result may differ because working model is modified
			List<MiniZincSolution> solutions = initialResult.getSolutions();
			allSolutions.addAll(solutions);
			finalSolutions = Streams.stream(solutions)
					.map(s -> findParetoOptimaForSolution(model, s))
					.stream().flatMap(s -> s)
					.collect(Collectors.toSet());
		} else {
			finalSolutions = Collections.emptySet();
		}

		// cleanup and finish
		workingModelManager.cleanup();
	}

	private Stream<MiniZincSolution> findParetoOptimaForSolution(String parentModel, MiniZincSolution parentSolution)
			throws IOException {
		// generate next model with getBetterConstraint
		String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, parentSolution) + ";";
		workingModelManager.replaceModel(parentModel);
		workingModelManager.appendToModel(updatedConstraint);
		String ownModel = workingModelManager.getModel();

		// find solutions for new model
		MiniZincResult result = runMiniZinc(workingModelManager.getFile(), dataFiles);
		if (!result.isSolvedAndValid()) return Stream.of(parentSolution);

		// recursively find optima for solutions we just found
		return Streams.stream(result.getSolutions())
				.filter(allSolutions::add) // ignore all solutions already seen, and ignore new solutions in the future
				.map(s -> findParetoOptimaForSolution(ownModel, s))
				.stream().flatMap(s -> s);
	}

	private MiniZincResult runMiniZinc(File miniZincFile, List<File> dataFiles) {
		if (isRandom()) {
			miniZincRunner.getConfiguration().setRandomSeed(randomSequence.nextInt(RANDOM_SEED_LIMIT));
		}
		return miniZincRunner.solve(miniZincFile, dataFiles, timeoutInSeconds);
	}

	private boolean isRandom() {
		return randomSequence != null;
	}

	public Set<MiniZincSolution> getAllSolutions() {
		return allSolutions;
	}
	public Set<MiniZincSolution> getFinalSolutions() { return finalSolutions; }
}

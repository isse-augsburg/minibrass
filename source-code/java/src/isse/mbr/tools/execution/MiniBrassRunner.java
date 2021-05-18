package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.lang3.stream.Streams;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private Set<MiniZincSolution> allSolutions;

	public MiniBrassRunner() {
		this(new MiniZincConfiguration());
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		setMiniZincConfiguration(configuration);
	}

	private void initializeBranchAndBound(File miniZincFile) throws IOException {
		// init fields
		workingModelManager = WorkingModelManager.create(miniZincFile, isDebug(), isDebug());
		allSolutions = new HashSet<>();
		if (randomize) {
			randomSequence = new Random(initialRandomSeed);
		}
		miniBrassCompiler.setMinizincOnly(true);
	}

	public Optional<MiniZincSolution> executeBranchAndBoundForSingleSolution(File miniZincFile, File miniBrassFile, List<File> dataFiles)
		throws MiniBrassParseException, IOException {
		getMiniZincRunnerConfiguration().setUseAllSolutions(false);
		Set<MiniZincSolution> solutions = executeBranchAndBound(miniZincFile, miniBrassFile, dataFiles);
		assert solutions.size() <= 1;
		return solutions.stream().findAny();
	}

	public Set<MiniZincSolution> executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws MiniBrassParseException, IOException {
		// initialize
		initializeBranchAndBound(miniZincFile);
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();

		// parse and compile MiniBrass
		String compiledMiniBrassCode = miniBrassCompiler.compileInMemory(miniBrassFile);
		// for domination search
		String getBetterConstraint = miniBrassCompiler.getUnderlyingParser().getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();

		// search for solutions
		workingModelManager.appendToModel(compiledMiniBrassCode);
		MiniZincResult initialResult = runMiniZinc(workingModelManager.getFile(), dataFiles);
		Set<MiniZincSolution> solutions;
		if (initialResult.isSolvedAndValid()) {
			String model = workingModelManager.getModel(); // do not inline – result may differ because working model is modified
			solutions = Streams.stream(initialResult.getSolutions())
					.map(s -> findParetoOptimaForSolution(model, dataFiles, s, getBetterConstraint, postProcessor))
					.stream().flatMap(s -> s)
					.collect(Collectors.toSet());
		} else {
			solutions = Collections.emptySet();
		}

		// cleanup and finish
		workingModelManager.cleanup();
		return solutions;
	}

	private Stream<MiniZincSolution> findParetoOptimaForSolution(String parentModel, List<File> dataFiles,
	                                                             MiniZincSolution parentSolution, String getBetterConstraint,
	                                                             MiniBrassPostProcessor postProcessor)
			throws IOException {
		String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, parentSolution) + ";";
		workingModelManager.replaceModel(parentModel);
		workingModelManager.appendToModel(updatedConstraint);
		String ownModel = workingModelManager.getModel();
		MiniZincResult result = runMiniZinc(workingModelManager.getFile(), dataFiles);
		if (!result.isSolvedAndValid()) return Stream.of(parentSolution);
		return Streams.stream(result.getSolutions())
				.filter(allSolutions::add) // ignore all solutions already seen, and ignore new solutions in the future
				.map(s -> findParetoOptimaForSolution(ownModel, dataFiles, s, getBetterConstraint, postProcessor))
				.stream().flatMap(s -> s);
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
		miniZincRunner.setConfiguration(configuration);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		this.miniZincRunner.setDebug(debug);
	}

	public Collection<MiniZincSolution> getAllSolutions() {
		return allSolutions;
	}

	public void setInitialRandomSeed(int initialRandomSeed) {
		this.initialRandomSeed = initialRandomSeed;
		this.randomize = true;
	}

	public void setTimeoutInSeconds(Integer timeout) {
		this.timeoutInSeconds = timeout;
	}
}

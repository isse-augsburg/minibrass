package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The MiniBrass runner is responsible for executing branch-and-bound or other
 * searches that involve executing several MiniZinc processes.
 * <p>
 * The {@link MiniBrassRunnerMain} provides a main function that executes this runner.
 * The {@link SingleUseMiniBrassRunner} is used for doing the branch and bound search.
 * </p>
 *
 *
 * @see MiniBrassRunnerMain
 * @see SingleUseMiniBrassRunner
 * @author alexander
 */
public class MiniBrassRunner {
	private final MiniZincRunner miniZincRunner = new MiniZincRunner();
	private final MiniBrassCompiler miniBrassCompiler = new MiniBrassCompiler();
	private boolean debug = false;
	private int initialRandomSeed = 1337;
	private boolean randomize = false;
	private Integer timeoutInSeconds = null; // in milliseconds


	private SingleUseMiniBrassRunner lastExecution;

	public MiniBrassRunner() {
		this(new MiniZincConfiguration());
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		setMiniZincConfiguration(configuration);
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
		lastExecution = SingleUseMiniBrassRunner.executeBranchAndBound(miniZincFile, miniBrassFile,
				dataFiles, miniZincRunner, miniBrassCompiler, debug, randomize ? initialRandomSeed : null, timeoutInSeconds);
		return lastExecution.getFinalSolutions();
	}

	public MiniZincConfiguration getMiniZincRunnerConfiguration() {
		return miniZincRunner.getConfiguration();
	}

	public void setMiniZincConfiguration(MiniZincConfiguration configuration) {
		miniZincRunner.setConfiguration(configuration);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		this.miniZincRunner.setDebug(debug);
	}

	public Collection<MiniZincSolution> getAllSolutions() {
		if (lastExecution == null) {
			throw new IllegalStateException("Solutions are only available after executing branch and bound search");
		}
		return lastExecution.getAllSolutions();
	}

	public void setInitialRandomSeed(int initialRandomSeed) {
		this.initialRandomSeed = initialRandomSeed;
		this.randomize = true;
	}

	@SuppressWarnings("unused") // the functionality is already implemented, why remove it then
	public void setTimeoutInSeconds(Integer timeout) {
		this.timeoutInSeconds = timeout;
	}
}

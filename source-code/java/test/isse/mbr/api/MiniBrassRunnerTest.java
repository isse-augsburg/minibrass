package isse.mbr.api;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import isse.mbr.tools.execution.MiniZincConfiguration;
import isse.mbr.tools.execution.MiniZincResult;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincVariable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;

public class MiniBrassRunnerTest {

	private MiniBrassRunner minibrassRunner;
	private File constraintModelFile;
	private File constraintModelFileForPareto;
	private File constraintModelFileForLex;

	private File preferenceModelFile;
	private File preferenceModelFileLex;
	private File preferenceModelFilePareto;

	@Before
	public void setup () {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(true);
		minibrassRunner = new MiniBrassRunner();
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);

		constraintModelFile = new File("test-models/classicNoSearch.mzn");
		constraintModelFileForPareto = new File("test-models/new-api/classicNoSearchPareto.mzn");
		constraintModelFileForLex = new File("test-models/new-api/classicNoSearchLex.mzn");

		preferenceModelFile = new File("test-models/classicNoSearch.mbr");
		preferenceModelFileLex = new File("test-models/new-api/classicNoSearchLex.mbr");
		preferenceModelFilePareto = new File("test-models/new-api/classicNoSearchPareto.mbr");

	}

	@Test
	public void testBasicSingleSolution() throws IOException, MiniBrassParseException {
		Optional<MiniZincSolution> solution = minibrassRunner.executeBranchAndBoundForSingleSolution(constraintModelFile,
				preferenceModelFile, Collections.emptyList());
		Assert.assertTrue("Should find a solution", solution.isPresent());
	}

	@Test
	public void testParetoSingleSolution() throws IOException, MiniBrassParseException {
		Optional<MiniZincSolution> solution = minibrassRunner.executeBranchAndBoundForSingleSolution(constraintModelFileForPareto,
				preferenceModelFilePareto, Collections.emptyList());
		Assert.assertTrue("Should find a solution", solution.isPresent());
	}

	@Test
	public void testLexSingleSolution() throws IOException, MiniBrassParseException {
		Optional<MiniZincSolution> solution = minibrassRunner.executeBranchAndBoundForSingleSolution(constraintModelFileForLex,
				preferenceModelFileLex, Collections.emptyList());
		Assert.assertTrue("Should find a solution", solution.isPresent());
	}

	@Test
	public void testBasicAllSolutions() throws IOException, MiniBrassParseException {
		Set<MiniZincSolution> solutions = minibrassRunner.executeBranchAndBound(constraintModelFile, preferenceModelFile,
				Collections.emptyList());
		Assert.assertFalse("Should find solutions", solutions.isEmpty());
	}

	@Test
	public void testParetoAllSolutions() throws IOException, MiniBrassParseException {
		Set<MiniZincSolution> solutions = minibrassRunner.executeBranchAndBound(constraintModelFileForPareto, preferenceModelFilePareto,
				Collections.emptyList());
		Assert.assertFalse("Should find solutions", solutions.isEmpty());
	}

	@Test
	public void testLexAllSolutions() throws IOException, MiniBrassParseException {
		Set<MiniZincSolution> solutions = minibrassRunner.executeBranchAndBound(constraintModelFileForLex, preferenceModelFileLex,
				Collections.emptyList());
		Assert.assertFalse("Should find solutions", solutions.isEmpty());
	}


	@Test
	public void testDebug() throws IOException, MiniBrassParseException {
		File modelFile = new File("test-models/DebugModel.mzn");
		File preferenceFile = new File("test-models/DebugModel.mbr");
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("OSICBC");
		configuration.setUseAllSolutions(false);
		minibrassRunner.setDebug(true);
		minibrassRunner.setMiniZincConfiguration(configuration);
		minibrassRunner.executeBranchAndBound(modelFile, preferenceFile, Collections.emptyList());
	}

}

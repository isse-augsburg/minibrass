package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import isse.mbr.tools.execution.MiniZincConfiguration;
import isse.mbr.tools.execution.MiniZincResult;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincVariable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;

public class VotingParetoTest {

	private MiniBrassRunner minibrassRunner;
	private File constraintModelFile;
	private File preferenceModelFile;
	
	@Before
	public void setup () {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		minibrassRunner = new MiniBrassRunner();
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);
		
		String constraintModel = "test-models/new-api/votePareto.mzn";
		constraintModelFile = new File(constraintModel);
		
		String preferenceModel = "test-models/new-api/votePareto.mbr";
		preferenceModelFile = new File(preferenceModel);
		
	}
	
	@Test
	public void testDebug() throws IOException, MiniBrassParseException {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		
		minibrassRunner.setDebug(true);
		minibrassRunner.setMiniZincConfiguration(configuration);
		minibrassRunner.executeBranchAndBound(constraintModelFile, preferenceModelFile, Collections.EMPTY_LIST);
	}
	
}

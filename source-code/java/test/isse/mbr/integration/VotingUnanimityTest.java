package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;
import isse.mbr.tools.execution.MiniZincConfiguration;

public class VotingUnanimityTest {

	private MiniBrassRunner minibrassRunner;
	private File constraintModelFile;
	private File preferenceModelFile;
	private File paretoPreferenceModelFile;
	private File condorcetPreferenceModelFile;
	
	@Before
	public void setup () {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		minibrassRunner = new MiniBrassRunner();
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);
		
		String constraintModel = "test-models/new-api/voteUnanimity.mzn";
		constraintModelFile = new File(constraintModel);
		
		String preferenceModel = "test-models/new-api/voteUnanimity.mbr";
		preferenceModelFile = new File(preferenceModel);
		
		paretoPreferenceModelFile = new File("test-models/new-api/voteUnanimityPareto.mbr");
		condorcetPreferenceModelFile = new File("test-models/new-api/voteUnanimityCondorcet.mbr");
		// The test consists of two objectives A and B that are enumerated as follows
		// (0, 8 | 2, 8 | 3, 9 | 5, 7)
		// two agents are maximizers that want to see the first objective A to go *up*
		// one agent wants the second objective B to go *down*
		// with weak unanimity, we get to see the solutions 
		// (0,8), 
		// (2,8) since it is better for two agents and equal for another
		// [ (3,9) is omitted, since the minimize is worse off when getting 9 instead of 8 ]
		// (5,7) is better for both
		
		// with strict unanimity (pareto) we'd only see the stream 
		// (0,8) (5,7)
		
		// with condorcet, we'd see all solutions (since a majority is always in favor)
	}
	
	@Test
	public void testUnanimity() throws IOException, MiniBrassParseException {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);
		minibrassRunner.executeBranchAndBound(constraintModelFile, preferenceModelFile, Collections.EMPTY_LIST);
		
		// number of solutions should be 3
		int numberSolutions = minibrassRunner.getAllSolutions().size();
		Assert.assertEquals(3, numberSolutions);
	}
	
	@Test
	public void testPareto() throws IOException, MiniBrassParseException {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);
		minibrassRunner.executeBranchAndBound(constraintModelFile, paretoPreferenceModelFile, Collections.EMPTY_LIST);
		
		// number of solutions should be 3
		int numberSolutions = minibrassRunner.getAllSolutions().size();
		Assert.assertEquals(2, numberSolutions);
	}
	
	@Test
	public void testCondorcet() throws IOException, MiniBrassParseException {
		MiniZincConfiguration configuration = new MiniZincConfiguration();
		configuration.setSolverId("Gecode");
		configuration.setUseAllSolutions(false);
		
		minibrassRunner.setDebug(false);
		minibrassRunner.setMiniZincConfiguration(configuration);
		minibrassRunner.executeBranchAndBound(constraintModelFile, condorcetPreferenceModelFile, Collections.EMPTY_LIST);
		
		// number of solutions should be 3
		int numberSolutions = minibrassRunner.getAllSolutions().size();
		Assert.assertEquals(4, numberSolutions);
	}
}

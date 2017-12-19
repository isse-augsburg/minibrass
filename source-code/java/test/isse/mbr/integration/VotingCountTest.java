package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;
import isse.mbr.parsing.MiniBrassVotingKeywords;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;
import isse.mbr.util.TestUtils;

public class VotingCountTest {

	String minibrassModel = "test-models/voteCountPVS.mbr";
	String minibrassCompiled = "test-models/voteCountPVS_o.mzn";
	String minizincModel = "test-models/voteCountPVS.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);
	}

	// TODO test case for wrong typing 
	
	@Test 
	public void testVotingCount() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());

		String outputString = TestUtils.readFile(output);
		String expectedString =String.format("int: %s = 4;", MiniBrassVotingKeywords.VOTER_COUNT+MiniBrassParser.VOTING_PREFIX+"1");
		
		Assert.assertTrue(outputString.contains(expectedString));
		
		// check our binding 
		expectedString = String.format("s = %s;", MiniBrassVotingKeywords.VOTER_COUNT+MiniBrassParser.VOTING_PREFIX+"1");
		Assert.assertTrue(outputString.contains(expectedString));		
		
		expectedString = String.format("names = %s;", MiniBrassVotingKeywords.VOTER_STRING_NAMES+MiniBrassParser.VOTING_PREFIX+"1");
		Assert.assertTrue(outputString.contains(expectedString));
		
		expectedString = "array[1..mbr_voter_count_MBR_VOT_1] of string: mbr_voter_string_names_MBR_VOT_1 = [\"agent1\", \"agent2\", \"agent3\", \"RefTo_agent3_MBR_LEX_2RefTo_agent1\"];";
		Assert.assertTrue(outputString.contains(expectedString));
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);

		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
	}

}

package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;


public class VotingCondorcetTest {
	

	String minibrassModel = "test-models/voteCondorcet.mbr";
	String minibrassModelNoWinner = "test-models/voteCondorcet_nowinner.mbr";
	String minibrassModelTest = "test-models/voteCondorcet_test.mbr";
	
	String minibrassCompiled = "test-models/voteCondorcet_o.mzn";
	String minizincModel = "test-models/voteCondorcet.mzn";
	String minizincModelTest = "test-models/voteCondorcet_test.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(false);

        launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
	}
	
	@Test
	public void testPvsRelationCondercetTest() throws IOException, MiniBrassParseException {
		// solution has draws in local results. result is good.
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelTest), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModelTest), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(2, listener.getSolutionCounter());
		Assert.assertEquals("2", listener.getLastSolution().get("a"));
	}
	
	@Test
	public void testPvsRelationCondercetNoWinner() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelNoWinner), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(2, listener.getSolutionCounter());
		Assert.assertEquals("2", listener.getLastSolution().get("a"));
	}

	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals("3", listener.getLastSolution().get("a"));
	}

}

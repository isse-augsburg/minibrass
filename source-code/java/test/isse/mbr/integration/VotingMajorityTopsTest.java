package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * This model represents a simple instance of majority tops voting
 * where we just aim at maximizing the number of agents that 
 * get their top value

 * For instance, the profile
 * 3 | 2 | 2
 * 1 | 1 | 3
 * 2 | 3 | 1
 * leads to 2 being chosen as two agents like it best
 * @author Alexander Schiendorfer
 *
 */
public class VotingMajorityTopsTest {

	String minibrassMajorityTopsModel = "test-models/voteMajorityTop.mbr";
	String minibrassCondorcetModel = "test-models/voteMajorityTopTest.mbr";

	String minibrassCompiled = "test-models/voteMajorityTop_o.mzn";
	String minizincModel = "test-models/voteMajorityTop.mzn";
	String minizincPureModel = "test-models/voteMajorityTopPure.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(false);
		launcher.setDebug(true);

        launcher.setMinizincGlobals("g12_fd");
		launcher.setFlatzincExecutable("flatzinc");
	}

	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassMajorityTopsModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals("2", listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetComparison() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassCondorcetModel), output);
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
	
	@Test 
	public void testPvsRelationPure() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setMinizincOnly(true);
		compiler.compile(new File(minibrassMajorityTopsModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincPureModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals("2", listener.getLastSolution().get("a"));
	}
}

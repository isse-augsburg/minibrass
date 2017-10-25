package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

public class VotingMajorityTopsTest {

	String minibrassModel = "test-models/voteMajorityTop.mbr";
	String minibrassModifiedModel = "test-models/voteMajorityTopTest.mbr";

	String minibrassCompiled = "test-models/voteMajorityTop_o.mzn";
	String minizincModel = "test-models/voteMajorityTop.mzn";
	String minizincPureModel = "test-models/voteMajorityTopPure.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
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
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals("2", listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetComparison() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModifiedModel), output);
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
		compiler.compile(new File(minibrassModel), output);
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

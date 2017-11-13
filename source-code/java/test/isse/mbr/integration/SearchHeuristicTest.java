package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * Verifies that we generate proper search heuristics 
 * @author Alexander Schiendorfer
 *
 */
public class SearchHeuristicTest {
	String minibrassModel = "test-models/testHeuristic.mbr";
	String minibrassCompiled = "test-models/testHeuristic_o.mzn";
	String minizincModel = "test-models/testHeuristic.mzn";
	String minizincActiveHeuristics = "test-models/testHeuristicMif.mzn";
	
	// for the weighted conversion 
	String minibrassWeightedModel = "test-models/testHeuristicWeighted.mbr";
	String minizincWeightedModel = "test-models/testHeuristicWeighted.mzn";
	 
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
	}

	@Test
	public void testHeuristics() throws IOException, MiniBrassParseException {
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
		
		Assert.assertEquals("3", listener.getLastSolution().get("x"));
		Assert.assertEquals("2", listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals("2..3", listener.getObjectives().get(obj));
	}
	

	@Test
	public void testActivatedHeuristics() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setGenHeuristics(true);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincActiveHeuristics), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
		Assert.assertEquals("3", listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals("{1,3}", listener.getObjectives().get(obj));
	}
	
	@Test
	public void testWeightedHeuristics() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setGenHeuristics(true);
		compiler.compile(new File(minibrassWeightedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincWeightedModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
		Assert.assertEquals("3", listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		Assert.assertEquals("3", listener.getObjectives().get("mbr_overall_ToWeighted_RefTo_cr1_"));
	}
}

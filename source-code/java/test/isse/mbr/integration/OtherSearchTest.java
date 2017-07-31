package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniZincKeywords;

/**
 * Makes sure that we allow non-dominated BAB and other searches 
 * @author Alexander Schiendorfer
 *
 */
public class OtherSearchTest {
	String minibrassModel = "test-models/testNonDomSearch.mbr";
	String minibrassCompiled = "test-models/testNonDomSearch_o.mzn";
	String minizincNonDomModel = "test-models/testNonDomSearch.mzn";
	String minizincDomModel = "test-models/testDomSearch.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
	}
	
	@Test
	public void testDom() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincDomModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = "topLevelObjective";
		Assert.assertEquals("3..3", listener.getObjectives().get(obj));
		Assert.assertEquals(2, listener.getSolutionCounter());
	}

	@Test
	public void testNonDom() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincNonDomModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = MiniZincKeywords.TOP_LEVEL_OBJECTIVE; 
		Assert.assertEquals("2..2", listener.getObjectives().get(obj));
		Assert.assertEquals(3, listener.getSolutionCounter());
	}

}

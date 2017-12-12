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
 * This tests invokes MiniZinc for a probabilistic CSP
 * It uses the global type definition in "def.mbr"
 * @author Alexander Schiendorfer
 *
 */
public class GlobalProbabilisticTest {

	String minibrassModel = "test-models/testProb.mbr";
	String minibrassMorphedModel = "test-models/testProbMorphed.mbr";
	String minibrassCompiled = "test-models/testProb_o.mzn";
	String minizincModel = "test-models/testProb.mzn";
	String minizincMorphedModel = "test-models/testProbMorphed.mzn";
	String minizincMorphedSearchModel = "test-models/testProbSearched.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		compiler.setMinizincOnly(true); // due to other complications with floats using minisearch
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
		launcher.setDebug(true);
	}

	@Test
	public void testProbabilistic() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals("1.0", listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testProbabilisticMorphed() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassMorphedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincMorphedModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals("0", listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testProbabilisticMorphedMinisearch() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setMinizincOnly(false);
		compiler.compile(new File(minibrassMorphedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincMorphedSearchModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals("0", listener.getObjectives().get("topLevelObjective"));
	}
}

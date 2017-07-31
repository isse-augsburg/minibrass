package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * Makes sure that we allow non-dominated BAB and other searches 
 * @author Alexander Schiendorfer
 *
 */
public class FreePVSMultiSetTest {
	String minibrassModel = "test-models/free-pvs.mbr";
	String minibrassCompiled = "test-models/free-pvs_o.mzn";
	String minizincModel = "test-models/free-pvs.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);
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
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		// for the objective, we observe the sequence {{3,3}}, {{3}}, {{1}}
		String obj = "topLevelObjective";
		Assert.assertEquals("3..3", listener.getObjectives().get(obj));
		Assert.assertEquals(2, listener.getSolutionCounter());
		
	}


}

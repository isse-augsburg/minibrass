package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.MiniZincLauncher;

/**
 * This test case uses probabilistic cost function networks 
 * and pure MiniZinc optimization
 * @author Alexander Schiendorfer
 *
 */
public class ProbabilisticCostFunctionNetwork {

	String minibrassModel = "test-models/testProbCfn.mbr";
	String minibrassCompiled = "test-models/testProbCfn_o.mzn";
	String minizincModel = "test-models/testProbCfn.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		compiler.setMinizincOnly(true);
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
	}

	@Test
	
	/**
	 * For, we only test correct MiniBrass compilation as the solvers do not work well with floats
	 * @throws IOException
	 * @throws MiniBrassParseException
	 */
	public void test() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);

		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		// Does not work due to float stuff
		
//		BasicTestListener listener = new BasicTestListener();
//		launcher.addMiniZincResultListener(listener);
//		//launcher.runMiniSearchModel(new File(minizincModel), null, 60);
//		launcher.runMiniZincModel(new File(minizincModel), null, 60);
//		
//		// 3. check solution
//		Assert.assertTrue(listener.isSolved());
//		Assert.assertTrue(listener.isOptimal());
//		
//		Assert.assertEquals("1", listener.getLastSolution().get("x"));
//		Assert.assertEquals("2", listener.getLastSolution().get("y"));
//		Assert.assertEquals("1", listener.getLastSolution().get("z"));
//		
//		// for the objective, we need to find out the variable name 
//		// instance was "cr1"
//		String obj = "topLevelObjective";
//		Assert.assertEquals("1", listener.getObjectives().get(obj));
	}

}

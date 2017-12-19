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

public class FuzzyTest {

	String minibrassModel = "test-models/fuzzy.mbr";
	String minibrassWorkaroundModel = "test-models/fuzzyWorkaround.mbr";
	String minibrassCompiled = "test-models/fuzzy_o.mzn";
	String minizincModel = "test-models/fuzzy.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	 
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
        launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
	}

	@Test
	public void testDirectTable() throws IOException, MiniBrassParseException {
		// This one currently only works with JaCoP
		// cause other solvers do not properly support float_array_element
		launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
		
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
		
		Assert.assertEquals("0", listener.getLastSolution().get("mainCourse"));
		Assert.assertEquals("0", listener.getLastSolution().get("wine"));
		Assert.assertEquals("0", listener.getLastSolution().get("lunch"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","fz1");
		Assert.assertEquals("1.0", listener.getObjectives().get(obj));
	}

	@Test
	public void testWorkaround() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassWorkaroundModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals("0", listener.getLastSolution().get("mainCourse"));
		Assert.assertEquals("0", listener.getLastSolution().get("wine"));
		Assert.assertEquals("0", listener.getLastSolution().get("lunch"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","fz1");
		Assert.assertEquals("1.0", listener.getObjectives().get(obj));
	}
}

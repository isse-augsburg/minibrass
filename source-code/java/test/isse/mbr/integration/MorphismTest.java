package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

public class MorphismTest {

	String minibrassModel = "test-models/classicMorphed.mbr";
	String minibrassCompiled = "test-models/classic_o.mzn";
	String minizincModel = "test-models/classicMorphed.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
	}

	@Test 
	public void testMorphism() throws IOException, MiniBrassParseException {
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
		
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
		Assert.assertEquals("2", listener.getLastSolution().get("y"));
		Assert.assertEquals("1", listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","ToWeighted_RefTo_cr1_");
		// we expect a violation (in weights) of 1
		Assert.assertEquals("1", listener.getObjectives().get(obj));
	}

}

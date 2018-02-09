package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import isse.mbr.integration.ExternalMorphismTest.Type;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

@RunWith(Parameterized.class)
public class FuzzyTest {

	String minibrassModel = "test-models/fuzzy.mbr";
	String minibrassWorkaroundModel = "test-models/fuzzyWorkaround.mbr";
	String minibrassCompiled = "test-models/fuzzy_o.mzn";
	String minizincModel = "test-models/fuzzy.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {ONE, TWO, THREE};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.ONE, "jacop", "fzn-jacop", "0", "0", "0", "1.0"},
				{Type.ONE, "gecode", "fzn-gecode", "0", "0", "0", "1.0"},
				{Type.ONE, "chuffed", "fzn-chuffed", "0", "0", "0", "1.0"},
				{Type.TWO, "jacop", "fzn-jacop", "0", "0", "0", "1.0"},
				{Type.TWO, "gecode", "fzn-gecode", "0", "0", "0", "1.0"},
				{Type.TWO, "chuffed", "fzn-chuffed", "0", "0", "0", "1.0"}
		});
	}

	private Type type;
	private String a, b, expected, expected2, expected3, expected4;

	public FuzzyTest(Type type, String a, String b, String expected,String expected2,String expected3,
			String expected4){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;this.expected2=expected2;this.expected3=expected3;
		this.expected4=expected4;
	}
	 
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	@Test
	public void testDirectTable() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.ONE);
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
		
		Assert.assertEquals(expected, listener.getLastSolution().get("mainCourse"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("wine"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("lunch"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","fz1");
		Assert.assertEquals(expected4, listener.getObjectives().get(obj));
	}

	@Test
	public void testWorkaround() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TWO);
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
		
		Assert.assertEquals(expected, listener.getLastSolution().get("mainCourse"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("wine"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("lunch"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","fz1");
		Assert.assertEquals(expected4, listener.getObjectives().get(obj));
	}
}

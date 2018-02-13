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

import isse.mbr.integration.DirectProductTest.Type;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

@RunWith(Parameterized.class)
public class ExternalMorphismTest {

	String minibrassModel = "test-models/classicMorphedExternal.mbr";
	String minibrassModelAll = "test-models/classicMorphedExternalAll.mbr";
	String minibrassModelNone = "test-models/classicMorphedExternalNone.mbr";
	
	String minibrassCompiled = "test-models/classic_o.mzn";
	String minizincModel = "test-models/classicMorphedSingle.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TESTMORPHISMNONE, TESTMORPHISMALL, TESTMORPHISM};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTMORPHISMNONE, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TESTMORPHISMNONE, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TESTMORPHISMNONE, "g12_fd", "flatzinc", "1", "2", "1", "1"},
				{Type.TESTMORPHISMNONE, "chuffed", "fzn-chuffed", "1", "2", "1", "1"},
				{Type.TESTMORPHISMALL, "jacop", "fzn-jacop", "3", "2", "1", "0"},
				{Type.TESTMORPHISMALL, "gecode", "fzn-gecode", "3", "2", "1", "0"},
				{Type.TESTMORPHISMALL, "g12_fd", "flatzinc", "3", "2", "1", "0"},
				{Type.TESTMORPHISMALL, "chuffed", "fzn-chuffed", "3", "2", "1", "0"},
				{Type.TESTMORPHISM, "jacop", "fzn-jacop", "1", "1", "1", "4"},
				{Type.TESTMORPHISM, "gecode", "fzn-gecode", "1", "1", "1", "4"},
				{Type.TESTMORPHISM, "g12_fd", "flatzinc", "1", "1", "1", "4"},
				{Type.TESTMORPHISM, "chuffed", "fzn-chuffed", "1", "1", "1", "4"}
		});
	}

	private Type type;
	private String a, b, expected, expected2, expected3, expected4;

	public ExternalMorphismTest(Type type, String a, String b, String expected,String expected2,String expected3,
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
	public void testMorphismNone() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTMORPHISM);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelNone), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		// we expect a violation (in weights) of 1
		
		Assert.assertEquals(expected4, listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testMorphismAll() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTMORPHISMALL);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelAll), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		// we expect a violation (in weights) of 0
		
		Assert.assertEquals(expected4, listener.getObjectives().get("topLevelObjective"));
	}

	@Test
	public void testMorphism() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTMORPHISMNONE);
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
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		// we expect a violation (in weights) of 1
		
		Assert.assertEquals(expected4, listener.getObjectives().get("topLevelObjective"));
	}

}

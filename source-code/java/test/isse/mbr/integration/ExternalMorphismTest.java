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
	enum Type {TEST_MORPHISM_NONE, TEST_MORPHISM_ALL, TEST_MORPHISM};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TEST_MORPHISM_NONE, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM_NONE, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM_NONE, "g12_fd", "flatzinc", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM_NONE, "chuffed", "fzn-chuffed", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM_ALL, "jacop", "fzn-jacop", "3", "2", "1", "0"},
				{Type.TEST_MORPHISM_ALL, "gecode", "fzn-gecode", "3", "2", "1", "0"},
				{Type.TEST_MORPHISM_ALL, "g12_fd", "flatzinc", "3", "2", "1", "0"},
				{Type.TEST_MORPHISM_ALL, "chuffed", "fzn-chuffed", "3", "2", "1", "0"},
				{Type.TEST_MORPHISM, "jacop", "fzn-jacop", "1", "1", "1", "4"},
				{Type.TEST_MORPHISM, "gecode", "fzn-gecode", "1", "1", "1", "4"},
				{Type.TEST_MORPHISM, "g12_fd", "flatzinc", "1", "1", "1", "4"},
				{Type.TEST_MORPHISM, "chuffed", "fzn-chuffed", "1", "1", "1", "4"}
		});
	}

	private Type type;
	private String mznGlobals, fznExec, expectedX, expectedY, expectedZ, expectedObjective;

	public ExternalMorphismTest(Type type, String a, String b, String expected,String expected2,String expected3,
			String expected4){
		this.type = type;
		this.mznGlobals=a; this.fznExec=b; this.expectedX=expected;this.expectedY=expected2;this.expectedZ=expected3;
		this.expectedObjective=expected4;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		
		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExec);
	}
	
	@Test
	public void testMorphismNone() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_MORPHISM);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
	
		// we expect a violation (in weights) of 1
		
		Assert.assertEquals(expectedObjective, listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testMorphismAll() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_MORPHISM_ALL);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
		
		// we expect a violation (in weights) of 0
		
		Assert.assertEquals(expectedObjective, listener.getObjectives().get("topLevelObjective"));
	}

	@Test
	public void testMorphism() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_MORPHISM_NONE);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		// we expect a violation (in weights) of 1
		
		Assert.assertEquals(expectedObjective, listener.getObjectives().get("topLevelObjective"));
	}

}

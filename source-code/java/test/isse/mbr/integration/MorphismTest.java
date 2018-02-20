package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
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
public class MorphismTest {

	String minibrassModel = "test-models/classicMorphed.mbr";
	String minibrassCompiled = "test-models/classic_o.mzn";
	String minizincModel = "test-models/classicMorphed.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TEST_MORPHISM};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TEST_MORPHISM, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM, "g12_fd", "flatzinc", "1", "2", "1", "1"},
				{Type.TEST_MORPHISM, "chuffed", "fzn-chuffed", "1", "2", "1", "1"}
		});
	}

	private Type type;
	private String mznGlobals, fznExec, expectedX, expectedY, expectedZ, expectedToWeightedObj;

	public MorphismTest(Type type, String a, String b, String expected,String expected2,String expected3,
			String expected4){
		this.type = type;
		this.mznGlobals=a; this.fznExec=b; this.expectedX=expected;this.expectedY=expected2;this.expectedZ=expected3;
		this.expectedToWeightedObj=expected4;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExec);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","ToWeighted_RefTo_cr1_");
		// we expect a violation (in weights) of 1
		Assert.assertEquals(expectedToWeightedObj, listener.getObjectives().get(obj));
	}

}

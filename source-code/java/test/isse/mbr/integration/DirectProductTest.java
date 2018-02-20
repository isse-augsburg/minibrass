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

import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * Executes the classical use case with weighted constraints for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class DirectProductTest { 

	String minibrassModel = "test-models/testMin.mbr";
	String minibrassTwoPVSModel = "test-models/testMinTwo.mbr";
	String minibrassCompiled = "test-models/testMin_o.mzn";
	String minizincModel = "test-models/testMin.mzn";
	String minizincTwoPVSModel = "test-models/testMinTwo.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TEST_SINGLE_PVS, TEST_TWO_PVS};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TEST_SINGLE_PVS, "jacop", "fzn-jacop", "true", "false", "0"},
				{Type.TEST_TWO_PVS, "jacop", "fzn-jacop", "false", "false", "0"},
				{Type.TEST_SINGLE_PVS, "gecode", "fzn-gecode", "true", "false", "0"},
				{Type.TEST_TWO_PVS, "gecode", "fzn-gecode", "false", "false", "0"},
				{Type.TEST_SINGLE_PVS, "g12_fd", "flatzinc", "true", "false", "0"},
				{Type.TEST_TWO_PVS, "g12_fd", "flatzinc", "false", "false", "0"},
				{Type.TEST_SINGLE_PVS, "chuffed", "fzn-chuffed", "true", "false", "0"},
				{Type.TEST_TWO_PVS, "chuffed", "fzn-chuffed", "false", "false", "0"}
		});
	}

	private Type type;
	private String mznGlobals, fznExec, expectedX, expectedY, expected3;

	public DirectProductTest(Type type, String a, String b, String expected,String expected2,String expected3){
		this.type = type;
		this.mznGlobals=a; this.fznExec=b; this.expectedX=expected;this.expectedY=expected2;this.expected3=expected3;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		
		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExec);
	}

	@Test
	public void testSinglePVS() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_SINGLE_PVS);
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
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected3, listener.getObjectives().get(obj));
	}
	
	@Test
	public void testTwoPVS() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_TWO_PVS);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassTwoPVSModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincTwoPVSModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected3, listener.getObjectives().get(obj));
	}
}

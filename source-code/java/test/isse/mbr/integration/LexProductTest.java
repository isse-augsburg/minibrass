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

/**
 * Executes the classical use case with weighted constraints for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class LexProductTest {

	String minibrassLexModel = "test-models/testLex.mbr";
	String minibrassLexInvModel = "test-models/testLexInv.mbr";
	String minibrassCompiled = "test-models/testLex_o.mzn";
	String minizincLexModel = "test-models/testLex.mzn";
	private MiniBrassCompiler compiler; 
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TEST_TWO_PVS, TEST_TWO_PVS_INVERTED};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TEST_TWO_PVS, "jacop", "fzn-jacop", "1", "3", "1", "5"},
				{Type.TEST_TWO_PVS, "gecode", "fzn-gecode", "1", "3", "1", "5"},
				{Type.TEST_TWO_PVS, "g12_fd", "flatzinc", "1", "3", "1", "5"},
				{Type.TEST_TWO_PVS, "chuffed", "fzn-chuffed", "1", "3", "1", "5"},
				{Type.TEST_TWO_PVS_INVERTED, "jacop", "fzn-jacop", "3", "1", "5", "1"},
				{Type.TEST_TWO_PVS_INVERTED, "gecode", "fzn-gecode", "3", "1", "5", "1"},
				{Type.TEST_TWO_PVS_INVERTED, "g12_fd", "flatzinc", "3", "1", "5", "1"},
				{Type.TEST_TWO_PVS_INVERTED, "chuffed", "fzn-chuffed", "3", "1", "5", "1"}
		});
	}

	private Type type;
	private String mznGlobals, flatzincExec, expectedX, expectedY, expectedCr1Objective, expectedCr2Objective;

	public LexProductTest(Type type, String a, String b, String expected,String expected2,String expected3,
			String expected4){
		this.type = type;
		this.mznGlobals=a; this.flatzincExec=b; this.expectedX=expected;this.expectedY=expected2;this.expectedCr1Objective=expected3;
		this.expectedCr2Objective=expected4;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		
		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(flatzincExec);
	}

	
	@Test
	public void testTwoPVS() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_TWO_PVS);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassLexModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincLexModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expectedCr1Objective, listener.getObjectives().get(obj));
		obj = CodeGenerator.encodeString("overall","cr2");
		Assert.assertEquals(expectedCr2Objective, listener.getObjectives().get(obj));
	}
	
	@Test
	public void testTwoPVSInverted() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_TWO_PVS_INVERTED);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassLexInvModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincLexModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expectedCr1Objective, listener.getObjectives().get(obj));
		obj = CodeGenerator.encodeString("overall","cr2");
		Assert.assertEquals(expectedCr2Objective, listener.getObjectives().get(obj));
	}
}

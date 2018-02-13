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
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;

import isse.mbr.integration.ExternalMorphismTest.Type;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * This tests invokes MiniZinc for a probabilistic CSP
 * It uses the global type definition in "def.mbr"
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class GlobalProbabilisticTest {

	String minibrassModel = "test-models/testProb.mbr";
	String minibrassMorphedModel = "test-models/testProbMorphed.mbr";
	String minibrassCompiled = "test-models/testProb_o.mzn";
	String minizincModel = "test-models/testProb.mzn";
	String minizincMorphedModel = "test-models/testProbMorphed.mzn";
	String minizincMorphedSearchModel = "test-models/testProbSearched.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TESTPROBABILISTIC, TESTPROBABILISTICMORPHED, TESTPROBABILISTICMORPHEDMINISEARCH};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTPROBABILISTIC, "jacop", "fzn-jacop", "1.0"},
				{Type.TESTPROBABILISTIC, "gecode", "fzn-gecode", "1.0"},
				{Type.TESTPROBABILISTIC, "chuffed", "fzn-chuffed", "1.0"},
				{Type.TESTPROBABILISTICMORPHED, "jacop", "fzn-jacop", "0"},
				{Type.TESTPROBABILISTICMORPHED, "gecode", "fzn-gecode", "0"},
				{Type.TESTPROBABILISTICMORPHED, "chuffed", "fzn-chuffed", "0"},
				{Type.TESTPROBABILISTICMORPHEDMINISEARCH, "jacop", "fzn-jacop", "0"},
				{Type.TESTPROBABILISTICMORPHEDMINISEARCH, "gecode", "fzn-gecode", "0"},
				{Type.TESTPROBABILISTICMORPHEDMINISEARCH, "chuffed", "fzn-chuffed", "0"}
		});
	}

	private Type type;
	private String a, b, expected;

	public GlobalProbabilisticTest(Type type, String a, String b, String expected){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		compiler.setMinizincOnly(true); // due to other complications with floats using minisearch
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
		launcher.setDebug(true);
	}

	@Test
	public void testProbabilistic() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPROBABILISTIC);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals(expected, listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testProbabilisticMorphed() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPROBABILISTICMORPHED);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassMorphedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincMorphedModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals(expected, listener.getObjectives().get("topLevelObjective"));
	}
	
	@Test
	public void testProbabilisticMorphedMinisearch() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPROBABILISTICMORPHEDMINISEARCH);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setMinizincOnly(false);
		compiler.compile(new File(minibrassMorphedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute MiniZinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincMorphedSearchModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
	
		// for the objective, we need to find out the variable name 
		Assert.assertEquals(expected, listener.getObjectives().get("topLevelObjective"));
	}
}

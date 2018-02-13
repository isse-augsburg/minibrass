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
 * Verifies that we generate proper search heuristics 
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class SearchHeuristicTest {
	String minibrassModel = "test-models/testHeuristic.mbr";
	String minibrassCompiled = "test-models/testHeuristic_o.mzn";
	String minizincModel = "test-models/testHeuristic.mzn";
	String minizincActiveHeuristics = "test-models/testHeuristicMif.mzn";
	
	// for the weighted conversion 
	String minibrassWeightedModel = "test-models/testHeuristicWeighted.mbr";
	String minizincWeightedModel = "test-models/testHeuristicWeighted.mzn";
	 
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TESTHEURISTICS, TESTACTIVATEDHEURISTICS, TESTWEIGHTEDHEURISTICS};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTHEURISTICS, "jacop", "fzn-jacop", "3", "2", "2..3"},
				{Type.TESTHEURISTICS, "gecode", "fzn-gecode", "3", "2", "2..3"},
				{Type.TESTHEURISTICS, "g12_fd", "flatzinc", "3", "2", "2..3"},
				{Type.TESTHEURISTICS, "chuffed", "fzn-chuffed", "3", "2", "2..3"},
				{Type.TESTACTIVATEDHEURISTICS, "jacop", "fzn-jacop", "1", "3", "{1,3}"},
				{Type.TESTACTIVATEDHEURISTICS, "gecode", "fzn-gecode", "1", "3", "{1,3}"},
				{Type.TESTACTIVATEDHEURISTICS, "g12_fd", "flatzinc", "1", "3", "{1,3}"},
				{Type.TESTACTIVATEDHEURISTICS, "chuffed", "fzn-chuffed", "1", "3", "{1,3}"},
				{Type.TESTWEIGHTEDHEURISTICS, "jacop", "fzn-jacop", "1", "3", "3"},
				{Type.TESTWEIGHTEDHEURISTICS, "gecode", "fzn-gecode", "1", "3", "3"},
				{Type.TESTWEIGHTEDHEURISTICS, "g12_fd", "flatzinc", "1", "3", "3"},
				{Type.TESTWEIGHTEDHEURISTICS, "chuffed", "fzn-chuffed", "1", "3", "3"}
		});
	}

	private Type type;
	private String a, b, expected, expected2, expected3;

	public SearchHeuristicTest(Type type, String a, String b, String expected,String expected2,String expected3){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;this.expected2=expected2;this.expected3=expected3;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	@Test
	public void testHeuristics() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTHEURISTICS);
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
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected3, listener.getObjectives().get(obj));
	}
	

	@Test
	public void testActivatedHeuristics() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTACTIVATEDHEURISTICS);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setGenHeuristics(true);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincActiveHeuristics), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected3, listener.getObjectives().get(obj));
	}
	
	@Test
	public void testWeightedHeuristics() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTWEIGHTEDHEURISTICS);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setGenHeuristics(true);
		compiler.compile(new File(minibrassWeightedModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincWeightedModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		Assert.assertEquals(expected3, listener.getObjectives().get("mbr_overall_ToWeighted_RefTo_cr1_"));
	}
}

package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import isse.mbr.integration.ExternalMorphismTest.Type;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * This model represents a simple instance of majority tops voting
 * where we just aim at maximizing the number of agents that 
 * get their top value

 * For instance, the profile
 * 3 | 2 | 2
 * 1 | 1 | 3
 * 2 | 3 | 1
 * leads to 2 being chosen as two agents like it best
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class VotingMajorityTopsTest {

	String minibrassMajorityTopsModel = "test-models/voteMajorityTop.mbr";
	String minibrassCondorcetModel = "test-models/voteMajorityTopTest.mbr";
	// we use two enhanced models in order to show different results for majority tops and condorcet
	String minibrassMajorityTopsEnhModel = "test-models/voteMajorityTop_enhanced.mbr";
	String minibrassCondorcetEnhModel = "test-models/voteCondorcet_enhanced.mbr";

	String minibrassCompiled = "test-models/voteMajorityTop_o.mzn";
	String minizincModel = "test-models/voteMajorityTop.mzn";
	String minizincPureModel = "test-models/voteMajorityTopPure.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
		enum Type {TESTPVSRELATIONMAJORITYTOPENHCOMPARISON, TESTPVSRELATIONCONDORCETENHCOMPARISON, TESTPVSRELATION,
			TESTPVSRELATIONCONDORCETCOMPARISON, TESTPVSRELATIONPURE};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.TESTPVSRELATIONMAJORITYTOPENHCOMPARISON, "jacop", "fzn-jacop", "3"},
					{Type.TESTPVSRELATIONMAJORITYTOPENHCOMPARISON, "gecode", "fzn-gecode", "3"},
					{Type.TESTPVSRELATIONMAJORITYTOPENHCOMPARISON, "g12_fd", "flatzinc", "3"},
					{Type.TESTPVSRELATIONMAJORITYTOPENHCOMPARISON, "chuffed", "fzn-chuffed", "3"},
					{Type.TESTPVSRELATIONCONDORCETENHCOMPARISON, "jacop", "fzn-jacop", "2"},
					{Type.TESTPVSRELATIONCONDORCETENHCOMPARISON, "gecode", "fzn-gecode", "2"},
					{Type.TESTPVSRELATIONCONDORCETENHCOMPARISON, "g12_fd", "flatzinc", "2"},
					{Type.TESTPVSRELATIONCONDORCETENHCOMPARISON, "chuffed", "fzn-chuffed", "2"},
					{Type.TESTPVSRELATION, "jacop", "fzn-jacop", "2"},
					{Type.TESTPVSRELATION, "gecode", "fzn-gecode", "2"},
					{Type.TESTPVSRELATION, "g12_fd", "flatzinc", "2"},
					{Type.TESTPVSRELATION, "chuffed", "fzn-chuffed", "2"},
					{Type.TESTPVSRELATIONCONDORCETCOMPARISON, "jacop", "fzn-jacop", "2"},
					{Type.TESTPVSRELATIONCONDORCETCOMPARISON, "gecode", "fzn-gecode", "2"},
					{Type.TESTPVSRELATIONCONDORCETCOMPARISON, "g12_fd", "flatzinc", "2"},
					{Type.TESTPVSRELATIONCONDORCETCOMPARISON, "chuffed", "fzn-chuffed", "2"},
					{Type.TESTPVSRELATIONPURE, "jacop", "fzn-jacop", "2"},
					{Type.TESTPVSRELATIONPURE, "gecode", "fzn-gecode", "2"},
					{Type.TESTPVSRELATIONPURE, "g12_fd", "flatzinc", "2"},
					{Type.TESTPVSRELATIONPURE, "chuffed", "fzn-chuffed", "2"}
			});
		}

		private Type type;
		private String a, b, expected;

		public VotingMajorityTopsTest(Type type, String a, String b, String expected){
			this.type = type;
			this.a=a; this.b=b; this.expected=expected;
		}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(false);
		launcher.setDebug(true);

        launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}
	
	@Test 
	public void testPvsRelationMajorityTopEnhComparison() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONMAJORITYTOPENHCOMPARISON);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassMajorityTopsEnhModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetEnhComparison() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONCONDORCETENHCOMPARISON);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassCondorcetEnhModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}

	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATION);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassMajorityTopsModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetComparison() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONCONDORCETCOMPARISON);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassCondorcetModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationPure() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONPURE);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.setMinizincOnly(true);
		compiler.compile(new File(minibrassMajorityTopsModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincPureModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}
}

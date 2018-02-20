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
		enum Type {TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON, TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON, TEST_PVS_RELATION,
			TEST_PVS_RELATION_CONDORCET_COMPARISON, TEST_PVS_RELATION_PURE};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON, "jacop", "fzn-jacop", "3"},
					{Type.TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON, "gecode", "fzn-gecode", "3"},
					{Type.TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON, "g12_fd", "flatzinc", "3"},
					{Type.TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON, "chuffed", "fzn-chuffed", "3"},
					{Type.TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON, "jacop", "fzn-jacop", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON, "gecode", "fzn-gecode", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON, "g12_fd", "flatzinc", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON, "chuffed", "fzn-chuffed", "2"},
					{Type.TEST_PVS_RELATION, "jacop", "fzn-jacop", "2"},
					{Type.TEST_PVS_RELATION, "gecode", "fzn-gecode", "2"},
					{Type.TEST_PVS_RELATION, "g12_fd", "flatzinc", "2"},
					{Type.TEST_PVS_RELATION, "chuffed", "fzn-chuffed", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_COMPARISON, "jacop", "fzn-jacop", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_COMPARISON, "gecode", "fzn-gecode", "2"},
		//			{Type.TEST_PVS_RELATION_CONDORCET_COMPARISON, "g12_fd", "flatzinc", "2"},
					{Type.TEST_PVS_RELATION_CONDORCET_COMPARISON, "chuffed", "fzn-chuffed", "2"},
					{Type.TEST_PVS_RELATION_PURE, "jacop", "fzn-jacop", "2"},
					{Type.TEST_PVS_RELATION_PURE, "gecode", "fzn-gecode", "2"},
					{Type.TEST_PVS_RELATION_PURE, "g12_fd", "flatzinc", "2"},
					{Type.TEST_PVS_RELATION_PURE, "chuffed", "fzn-chuffed", "2"}
			});
		}

		private Type type;
		private String a, b, expectedA;

		public VotingMajorityTopsTest(Type type, String a, String b, String expected){
			this.type = type;
			this.a=a; this.b=b; this.expectedA=expected;
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
		Assume.assumeTrue(type == Type.TEST_PVS_RELATION_MAJORITY_TOP_ENH_COMPARISON);
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
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetEnhComparison() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_PVS_RELATION_CONDORCET_ENH_COMPARISON);
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
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}

	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_PVS_RELATION);
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
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationCondorcetComparison() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_PVS_RELATION_CONDORCET_COMPARISON);
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
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}
	
	@Test 
	public void testPvsRelationPure() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_PVS_RELATION_PURE);
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
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}
}

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
public class VoteProdTest {

	String minibrassModel = "test-models/voteProd.mbr";
	String minibrassInvertModel = "test-models/voteProdInvert.mbr";
	String minibrassCompiled = "test-models/voteProd_o.mzn";
	String minizincModel = "test-models/voteProd.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
		enum Type {TEST_VOTING_THEN_LEX_TEST, TEST_LEX_THEN_VOTING};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.TEST_VOTING_THEN_LEX_TEST, "jacop", "fzn-jacop", "3"},
					{Type.TEST_VOTING_THEN_LEX_TEST, "gecode", "fzn-gecode", "3"},
					{Type.TEST_VOTING_THEN_LEX_TEST, "g12_fd", "flatzinc", "3"},
					{Type.TEST_VOTING_THEN_LEX_TEST, "chuffed", "fzn-chuffed", "3"},
					{Type.TEST_LEX_THEN_VOTING, "jacop", "fzn-jacop", "1"},
					{Type.TEST_LEX_THEN_VOTING, "gecode", "fzn-gecode", "1"},
					{Type.TEST_LEX_THEN_VOTING, "g12_fd", "flatzinc", "1"},
					{Type.TEST_LEX_THEN_VOTING, "chuffed", "fzn-chuffed", "1"}
			});
		}

		private Type type;
		private String mznGlobals, fznExec, expectedA;

		public VoteProdTest(Type type, String a, String b, String expected){
			this.type = type;
			this.mznGlobals=a; this.fznExec=b; this.expectedA=expected;
		}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);

		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExec);
	}

	@Test 
	public void testVotingThenLex() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_VOTING_THEN_LEX_TEST);
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
		
		Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}

	@Test 
	public void testLexThenVoting() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_LEX_THEN_VOTING);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassInvertModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(2, listener.getSolutionCounter());
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}
}

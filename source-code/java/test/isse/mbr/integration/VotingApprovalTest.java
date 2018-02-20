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
public class VotingApprovalTest {

	String minibrassModel = "test-models/voteApproval.mbr";
	String minibrassWrongTypeModel ="test-models/voteApproval-WrongType.mbr";
	String minibrassCompiled = "test-models/voteApproval_o.mzn";
	String minizincModel = "test-models/voteApproval.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
		enum Type {TEST_VOTE_APPROVAL_CORRECT, TEST_VOTE_APPROVAL_WRONG};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.TEST_VOTE_APPROVAL_CORRECT, "jacop", "fzn-jacop", "2"},
					{Type.TEST_VOTE_APPROVAL_CORRECT, "gecode", "fzn-gecode", "2"},
					{Type.TEST_VOTE_APPROVAL_CORRECT, "g12_fd", "flatzinc", "2"},
					{Type.TEST_VOTE_APPROVAL_CORRECT, "chuffed", "fzn-chuffed", "2"},
					{Type.TEST_VOTE_APPROVAL_WRONG, "jacop", "fzn-jacop", "2"},
					{Type.TEST_VOTE_APPROVAL_WRONG, "gecode", "fzn-gecode", "2"},
					{Type.TEST_VOTE_APPROVAL_WRONG, "g12_fd", "flatzinc", "2"},
					{Type.TEST_VOTE_APPROVAL_WRONG, "chuffed", "fzn-chuffed", "2"}
			});
		}

		private Type type;
		private String mznGlobals, fznExec, expectedA;

		public VotingApprovalTest(Type type, String a, String b, String expected){
			this.type = type;
			this.mznGlobals=a; this.fznExec=b; this.expectedA=expected;
		}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);

        launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExec);
	}
	
	@Test(expected = MiniBrassParseException.class) 
	/**
	 * This test is expected to throw an Exception as there is an integer type in an approval voting
	 * @throws IOException
	 * @throws MiniBrassParseException
	 */
	public void testVotingApprovalWrongType() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_VOTE_APPROVAL_WRONG);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassWrongTypeModel), output);
	}
	
	@Test 
	public void testVotingApprovalCorrect() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_VOTE_APPROVAL_CORRECT);
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
		
		Assert.assertEquals(3, listener.getSolutionCounter());
		Assert.assertEquals(expectedA, listener.getLastSolution().get("a"));
	}

}

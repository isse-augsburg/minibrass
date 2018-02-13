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
		enum Type {TESTVOTINGTHENTEST, TESTLEXTHENVOTING};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.TESTVOTINGTHENTEST, "jacop", "fzn-jacop", "3"},
					{Type.TESTVOTINGTHENTEST, "gecode", "fzn-gecode", "3"},
					{Type.TESTVOTINGTHENTEST, "g12_fd", "flatzinc", "3"},
					{Type.TESTVOTINGTHENTEST, "chuffed", "fzn-chuffed", "3"},
					{Type.TESTLEXTHENVOTING, "jacop", "fzn-jacop", "1"},
					{Type.TESTLEXTHENVOTING, "gecode", "fzn-gecode", "1"},
					{Type.TESTLEXTHENVOTING, "g12_fd", "flatzinc", "1"},
					{Type.TESTLEXTHENVOTING, "chuffed", "fzn-chuffed", "1"}
			});
		}

		private Type type;
		private String a, b, expected;

		public VoteProdTest(Type type, String a, String b, String expected){
			this.type = type;
			this.a=a; this.b=b; this.expected=expected;
		}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);

		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	@Test 
	public void testVotingThenLex() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTVOTINGTHENTEST);
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
		Assert.assertEquals("3", listener.getLastSolution().get("a"));
	}

	@Test 
	public void testLexThenVoting() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTLEXTHENVOTING);
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
		Assert.assertEquals("1", listener.getLastSolution().get("a"));
	}
}

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

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

@RunWith(Parameterized.class)
public class VotingCondorcetTest {
	

	String minibrassModel = "test-models/voteCondorcet.mbr";
	String minibrassModelNoWinner = "test-models/voteCondorcet_nowinner.mbr";
	String minibrassModelTest = "test-models/voteCondorcet_test.mbr";
	
	String minibrassCompiled = "test-models/voteCondorcet_o.mzn";
	String minizincModel = "test-models/voteCondorcet.mzn";
	String minizincModelTest = "test-models/voteCondorcet_test.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TESTPVSRELATIONCONDORCETTEST, TESTPVSRELATIONCONDORCETNOWINNER, TESTPVSRELATION};
    @Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][] {
          {Type.TESTPVSRELATIONCONDORCETTEST, "jacop", "fzn-jacop", "2"},
          {Type.TESTPVSRELATIONCONDORCETNOWINNER, "jacop", "fzn-jacop", "2"},
          {Type.TESTPVSRELATION, "jacop", "fzn-jacop", "3"},
          {Type.TESTPVSRELATIONCONDORCETTEST, "gecode", "fzn-gecode", "2"},
          {Type.TESTPVSRELATIONCONDORCETNOWINNER, "gecode", "fzn-gecode", "2"},
          {Type.TESTPVSRELATION, "gecode", "fzn-gecode", "3"},
          {Type.TESTPVSRELATIONCONDORCETTEST, "g12_fd", "flatzinc", "2"},
          {Type.TESTPVSRELATIONCONDORCETNOWINNER, "g12_fd", "flatzinc", "2"},
          {Type.TESTPVSRELATION, "g12_fd", "flatzinc", "3"},
          {Type.TESTPVSRELATIONCONDORCETTEST, "chuffed", "fzn-chuffed", "2"},
          {Type.TESTPVSRELATIONCONDORCETNOWINNER, "chuffed", "fzn-chuffed", "2"},
          {Type.TESTPVSRELATION, "chuffed", "fzn-chuffed", "3"}
        });
    }

    private Type type;
    private String a, b, expected;

    public VotingCondorcetTest(Type type, String a, String b, String expected){
        this.type = type;
        this.a=a; this.b=b; this.expected=expected;
    }
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(false);

        //launcher.setMinizincGlobals("jacop");
		//launcher.setFlatzincExecutable("fzn-jacop");
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}
	
	@Test
	public void testPvsRelationCondercetTest() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONCONDORCETTEST);
		// solution has draws in local results. result is good.
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelTest), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModelTest), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(2, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}
	
	@Test
	public void testPvsRelationCondercetNoWinner() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATIONCONDORCETNOWINNER);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelNoWinner), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		//Assert.assertEquals(2, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}

	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTPVSRELATION);
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
		
		//Assert.assertEquals(4, listener.getSolutionCounter());
		Assert.assertEquals(expected, listener.getLastSolution().get("a"));
	}

}

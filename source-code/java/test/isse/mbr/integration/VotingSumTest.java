package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import isse.mbr.integration.VotingProductsTest.Type;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

@RunWith(Parameterized.class)
public class VotingSumTest {

	String minibrassModel = "test-models/voteSum.mbr";
	String minibrassCompiled = "test-models/voteSum_o.mzn";
	String minizincModel = "test-models/voteSum.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;

	// parameterized test stuff
	enum Type {TESTPVSRELATION};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTPVSRELATION, "jacop", "fzn-jacop"},
				{Type.TESTPVSRELATION, "gecode", "fzn-gecode"},
				{Type.TESTPVSRELATION, "g12_fd", "flatzinc"},
				{Type.TESTPVSRELATION, "chuffed", "fzn-chuffed"}
		});
	}

	private Type type;
	private String a, b;

	public VotingSumTest(Type type, String a, String b){
		this.type = type;
		this.a=a; this.b=b; 
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

	// TODO test case for wrong typing 
	
	@Test 
	public void testPvsRelation() throws IOException, MiniBrassParseException {
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

}

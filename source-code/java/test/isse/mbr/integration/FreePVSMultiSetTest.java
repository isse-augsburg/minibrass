package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
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
 * Makes sure that we allow non-dominated BAB and other searches 
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class FreePVSMultiSetTest {
	String minibrassModel = "test-models/free-pvs.mbr";
	String minibrassCompiled = "test-models/free-pvs_o.mzn";
	String minizincModel = "test-models/free-pvs.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {ONE, TWO, THREE};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.ONE, "jacop", "fzn-jacop", "[0, 0, 2]", "[0, 0, 1]", "[1, 0, 0]"},
				{Type.ONE, "gecode", "fzn-gecode", "[0, 0, 2]", "[0, 0, 1]", "[1, 0, 0]"},
				{Type.ONE, "g12_fd", "flatzinc", "[0, 0, 2]", "[0, 0, 1]", "[1, 0, 0]"},
				{Type.ONE, "chuffed", "fzn-chuffed", "[0, 0, 2]", "[0, 0, 1]", "[1, 0, 0]"}
		});
	}

	private Type type;
	private String a, b, expected, expected2, expected3, expected4;

	public FreePVSMultiSetTest(Type type, String a, String b, String expected,String expected2,String expected3){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;this.expected2=expected2;this.expected3=expected3;
	}


	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
		//launcher.setUseDefault(false);
		//launcher.setDebug(true);
		
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}
	
	@Test
	@Ignore
	public void testFreePvs() throws IOException, MiniBrassParseException {
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
		
		// for the objective, we observe the sequence {{3,3}}, {{3}}, {{1}}
		String obj = "topLevelObjective";
		String[] expecteds = new String[] {expected, expected2, expected3 };

		// 3 "actual" solutions and one optimality notification
		Assert.assertEquals(4, listener.getSolutionCounter());
		int index = 0;
		for(String expected : expecteds) {
			String actual =	 listener.getObjectiveSequences().get(obj).get(index);
			Assert.assertEquals(expected, actual);
			++index;
		}
		System.out.println("WAAAAAAAAAAAAAAAAAAAAAAAAAH");

		
	}


}

package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
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
 * Executes the classical use case for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */ 
@RunWith(Parameterized.class)
public class InvalidDatesTest {

	String minibrassModel = "test-models/invalidDatesTest.mbr";
	String minibrassCompiled = "test-models/invalidDatesTest_o.mzn";
	String minizincModel = "test-models/invalidDatesTest.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
		enum Type {ONE, TWO, THREE};
		@Parameters
		public static Collection<Object[]> data(){
			return Arrays.asList(new Object[][] {
					{Type.ONE, "jacop", "fzn-jacop", "1"},
					{Type.ONE, "gecode", "fzn-gecode", "1"},
					{Type.ONE, "g12_fd", "flatzinc", "1"},
					{Type.ONE, "chuffed", "fzn-chuffed", "1"}
			});
		}

		private Type type;
		private String a, b, expected;

		public InvalidDatesTest(Type type, String a, String b, String expected){
			this.type = type;
			this.a=a; this.b=b; this.expected=expected;
		}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
		
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	@Test
	public void test() throws IOException, MiniBrassParseException {
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
		Assert.assertNotEquals(expected, listener.getLastSolution().get("x"));
	
	}

}

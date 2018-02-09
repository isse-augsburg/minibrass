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

import isse.mbr.integration.ExternalMorphismTest.Type;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

@RunWith(Parameterized.class)
public class PvsRelationTest {

	String minibrassModel = "test-models/pvsRelation.mbr";
	String minibrassCompiled = "test-models/pvsRelation_o.mzn";
	String minizincModel = "test-models/pvsRelation.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;

	// parameterized test stuff
	enum Type {ONE, TWO, THREE};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.ONE, "jacop", "fzn-jacop", "3"},
				{Type.ONE, "gecode", "fzn-gecode", "3"},
				{Type.ONE, "g12_fd", "flatzinc", "3"},
				{Type.ONE, "chuffed", "fzn-chuffed", "3"}
		});
	}

	private Type type;
	private String a, b, expected;

	public PvsRelationTest(Type type, String a, String b, String expected){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

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
		
		Assert.assertEquals("3", listener.getLastSolution().get("a"));
	}

}

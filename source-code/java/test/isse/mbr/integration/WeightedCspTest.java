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
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * Executes the classical use case with weighted constraints for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class WeightedCspTest {

	String minibrassModelParam = "test-models/weightedParams.mbr";
	String minibrassModelAnnot = "test-models/weightedAnnot.mbr";
	String minibrassCompiled = "test-models/classic_o.mzn";
	String minizincModel = "test-models/classic.mzn";
	private MiniBrassCompiler compiler; 
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {TESTWEIGHTEDASPARAM, TESTWEIGHTEDASANNOTATION};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTWEIGHTEDASPARAM, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASPARAM, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASPARAM, "g12_fd", "flatzinc", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASPARAM, "chuffed", "fzn-chuffed", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASANNOTATION, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASANNOTATION, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASANNOTATION, "g12_fd", "flatzinc", "1", "2", "1", "1"},
				{Type.TESTWEIGHTEDASANNOTATION, "chuffed", "fzn-chuffed", "1", "2", "1", "1"}
		});
	}

	private Type type;
	private String a, b, expected, expected2, expected3, expected4;

	public WeightedCspTest(Type type, String a, String b, String expected,String expected2,String expected3,
			String expected4){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;this.expected2=expected2;this.expected3=expected3;
		this.expected4=expected4;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
	
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	@Test
	public void testWeightedAsParam() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTWEIGHTEDASPARAM);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelParam), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected4, listener.getObjectives().get(obj));
	}

	@Test
	public void testWeightedAsAnnotation() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTWEIGHTEDASANNOTATION);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModelAnnot), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals(expected, listener.getLastSolution().get("x"));
		Assert.assertEquals(expected2, listener.getLastSolution().get("y"));
		Assert.assertEquals(expected3, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expected4, listener.getObjectives().get(obj));
	}
}

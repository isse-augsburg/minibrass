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
	enum Type {TEST_WEIGHTED_AS_PARAM, TEST_WEIGHTED_AS_ANNOTATION};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TEST_WEIGHTED_AS_PARAM, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TEST_WEIGHTED_AS_PARAM, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TEST_WEIGHTED_AS_PARAM, "g12_fd", "flatzinc", "1", "2", "1", "1"},
//				{Type.TEST_WEIGHTED_AS_PARAM, "chuffed", "fzn-chuffed", "1", "2", "1", "1"},
				{Type.TEST_WEIGHTED_AS_ANNOTATION, "jacop", "fzn-jacop", "1", "2", "1", "1"},
				{Type.TEST_WEIGHTED_AS_ANNOTATION, "gecode", "fzn-gecode", "1", "2", "1", "1"},
				{Type.TEST_WEIGHTED_AS_ANNOTATION, "g12_fd", "flatzinc", "1", "2", "1", "1"},
//				{Type.TEST_WEIGHTED_AS_ANNOTATION, "chuffed", "fzn-chuffed", "1", "2", "1", "1"}
		});
	}

	private Type type;
	private String mznGlobals, fznExecutable, expectedX, expectedY, expectedZ, expectedCrObj;

	public WeightedCspTest(Type type, String mznGlobals, String fznExecutable, String expectedX,String expectedY,String expectedZ,
			String expectedCrObj){
		this.type = type;
		this.mznGlobals=mznGlobals; this.fznExecutable=fznExecutable; this.expectedX=expectedX;this.expectedY=expectedY;this.expectedZ=	expectedZ;
		this.expectedCrObj=expectedCrObj;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
	
		launcher = new MiniZincLauncher();
		launcher.setMinizincGlobals(mznGlobals);
		launcher.setFlatzincExecutable(fznExecutable);
	}

	@Test
	public void testWeightedAsParam() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_WEIGHTED_AS_PARAM);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expectedCrObj, listener.getObjectives().get(obj));
	}

	@Test
	public void testWeightedAsAnnotation() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TEST_WEIGHTED_AS_ANNOTATION);
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
		
		Assert.assertEquals(expectedX, listener.getLastSolution().get("x"));
		Assert.assertEquals(expectedY, listener.getLastSolution().get("y"));
		Assert.assertEquals(expectedZ, listener.getLastSolution().get("z"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals(expectedCrObj, listener.getObjectives().get(obj));
	}
}

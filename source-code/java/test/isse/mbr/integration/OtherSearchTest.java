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
import isse.mbr.parsing.MiniZincKeywords;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

/**
 * Makes sure that we allow non-dominated BAB and other searches 
 * @author Alexander Schiendorfer
 *
 */
@RunWith(Parameterized.class)
public class OtherSearchTest {
	String minibrassModel = "test-models/testNonDomSearch.mbr";
	String minibrassCompiled = "test-models/testNonDomSearch_o.mzn";
	String minizincNonDomModel = "test-models/testNonDomSearch.mzn";
	String minizincDomModel = "test-models/testDomSearch.mzn";
	
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	// parameterized test stuff
	enum Type {ONE, TWO, THREE};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.ONE, "jacop", "fzn-jacop", "3..3", 2},
				{Type.ONE, "gecode", "fzn-gecode", "3..3", 2},
				{Type.ONE, "g12_fd", "flatzinc", "3..3", 2},
				{Type.TWO, "jacop", "fzn-jacop", "2..2", 3},
				{Type.TWO, "gecode", "fzn-gecode", "2..2", 3},
				{Type.TWO, "g12_fd", "flatzinc", "2..2", 3}
		});
	}

	private Type type;
	private String a, b, expected;
	private int expected2;

	public OtherSearchTest(Type type, String a, String b, String expected,int expected2){
		this.type = type;
		this.a=a; this.b=b; this.expected=expected;this.expected2=expected2;
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		launcher = new MiniZincLauncher();
	//	launcher.setUseDefault(true);
		launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}
	
	@Test
	public void testDom() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.ONE);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincDomModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = "topLevelObjective";
		Assert.assertEquals(expected, listener.getObjectives().get(obj));
		Assert.assertEquals(expected2, listener.getSolutionCounter());
	}

	@Test
	public void testNonDom() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TWO);
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincNonDomModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = MiniZincKeywords.TOP_LEVEL_OBJECTIVE; 
		Assert.assertEquals(expected, listener.getObjectives().get(obj));
		Assert.assertEquals(expected2, listener.getSolutionCounter());
	}

}

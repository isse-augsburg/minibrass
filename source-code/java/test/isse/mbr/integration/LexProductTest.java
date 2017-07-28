package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * Executes the classical use case with weighted constraints for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */
public class LexProductTest {

	String minibrassLexModel = "test-models/testLex.mbr";
	String minibrassLexInvModel = "test-models/testLexInv.mbr";
	String minibrassCompiled = "test-models/testLex_o.mzn";
	String minizincLexModel = "test-models/testLex.mzn";
	private MiniBrassCompiler compiler; 
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
	}

	
	@Test
	public void testTwoPVS() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassLexModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincLexModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
		Assert.assertEquals("3", listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals("1", listener.getObjectives().get(obj));
		obj = CodeGenerator.encodeString("overall","cr2");
		Assert.assertEquals("5", listener.getObjectives().get(obj));
	}
	
	@Test
	public void testTwoPVSInverted() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassLexInvModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minisearch
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincLexModel), null, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertTrue(listener.isOptimal());
		
		Assert.assertEquals("3", listener.getLastSolution().get("x"));
		Assert.assertEquals("1", listener.getLastSolution().get("y"));
		
		// for the objective, we need to find out the variable name 
		// instance was "cr1"
		String obj = CodeGenerator.encodeString("overall","cr1");
		Assert.assertEquals("5", listener.getObjectives().get(obj));
		obj = CodeGenerator.encodeString("overall","cr2");
		Assert.assertEquals("1", listener.getObjectives().get(obj));
	}
}

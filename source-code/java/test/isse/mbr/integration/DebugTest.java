package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.MiniZincLauncher;

/**
 * Executes a model for debugging purposes 
 * @author Alexander Schiendorfer
 *
 */ 
public class DebugTest {

	String minizincModel = "test-models/DebugModel.mzn";
	String minibrassModel = "test-models/DebugModel.mbr";
	String minibrassCompiled = "test-models/smallExample_o.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		launcher = new MiniZincLauncher();
		launcher.setDebug(true);
	}

	@Test
	public void test() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);
		
	}

}

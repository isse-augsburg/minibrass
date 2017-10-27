package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * Executes a model for debugging purposes 
 * @author Alexander Schiendorfer
 *
 */ 
public class DebugTest {

	String minibrassModel = "test-models/DebugModel.mbr";
	String minibrassCompiled = "test-models/debug_o.mzn";
	private MiniBrassCompiler compiler;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
	}

	@Test
	public void test() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
	}

}

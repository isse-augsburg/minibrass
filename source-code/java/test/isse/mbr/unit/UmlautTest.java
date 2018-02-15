package isse.mbr.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import isse.mbr.model.MiniBrassAST;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParser;


/**
 * Executes the classical use case for soft constraints to make 
 * sure everything compiles smoothly and we get the correct optimum
 * @author Alexander Schiendorfer
 *
 */ 
public class UmlautTest {

	String minibrassModel = "test-models/Umlaut.mbr";
	String minibrassCompiled = "test-models/Umlaut_o.mzn";
	String expectedResult = "test-models/Umlaut_expected_o.mzn";
	//String minizincModel = "test-models/classic.mzn";
	private MiniBrassCompiler compiler;
	//private MiniZincLauncher launcher;
	private String expected;
	
	
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler(true);
		//launcher = new MiniZincLauncher();
		expected = getFileContent(expectedResult);
	}
	
	@Test
	public void test() throws Exception {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		String result = getFileContent(minibrassCompiled);
		
		// 2. look at compiled file
		Assert.assertEquals(result, expected);
	}

	private String getFileContent(String filename) throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(filename)));		
		return content;
	}


}













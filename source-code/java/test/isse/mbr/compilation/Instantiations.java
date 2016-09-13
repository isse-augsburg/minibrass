package isse.mbr.compilation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * This test case investigates if we reliably detect incorrect statements when instantiating PVS
 * @author Alexander Schiendorfer
 *
 */
public class Instantiations {

	private String typeWithDef;
	private File tempFile;
	private File tempOutput;
	
	@Before
	public void setUp() throws Exception {
		typeWithDef = "type WeightedCsp = PVSType<bool, int> = "
		+ " params {"
		+ " int: k :: default('1000'); "
		+ " array[1..nScs] of 1..k: weights :: default('1');"
		+ "} in  "
		+ "instantiates with \"../mbr_types/weighted_type.mzn\" {"
		+ "  times -> weighted_sum;"
		+ "  is_worse -> is_worse_weighted;"
		+ "  top -> 0;"
		+ "};";
		
		tempFile = File.createTempFile("testing", ".mbr");
		tempOutput = File.createTempFile("testing_o", ".mzn");
	}
	
	@After
	public void tearDown() {
		tempFile.delete();
		tempOutput.delete();
	}

	@Test(expected=MiniBrassParseException.class)
	public void testDoubleSoftConstraint() throws IOException, MiniBrassParseException {
		String instantiation = "PVS: cr1 = new WeightedCsp(\"cr1\") {"
				+ "   soft-constraint c1: 'x + 1 = y' ;"
				+ "   soft-constraint c2: 'z = y + 2' ;"
				+ "   soft-constraint c2: 'x + y <= 3' ;"
				+ "   k : '20';"
				+ "   weights : '[2, 1, 1]';"
				+ "}; "
				+ "solve cr1;";
		
		String combined = typeWithDef + instantiation;
		writeSilent(tempFile, combined);
		
		MiniBrassCompiler compiler = new MiniBrassCompiler();
		compiler.compile(tempFile, tempOutput);
 
		Assert.assertTrue(tempOutput.exists());
	}
		private void writeSilent(File tempFile2, String combined) {
		FileWriter fw;
		try {
			fw = new FileWriter(tempFile2);
			fw.write(combined);
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

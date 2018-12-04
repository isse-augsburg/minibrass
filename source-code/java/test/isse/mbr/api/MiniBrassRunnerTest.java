package isse.mbr.api;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;

public class MiniBrassRunnerTest {

	private MiniBrassRunner minibrassRunner;
	private File constraintModelFile;
	private File constraintModelFileForPareto;
	private File constraintModelFileForLex;
	
	private File preferenceModelFile;
	private File preferenceModelFileLex;
	private File preferenceModelFilePareto;
	
	@Before
	public void setup () {
		minibrassRunner = new MiniBrassRunner();
		String constraintModel = "test-models/classicNoSearch.mzn";
		constraintModelFile = new File(constraintModel);
		constraintModelFileForPareto = new File("test-models/new-api/classicNoSearchPareto.mzn");
		constraintModelFileForLex = new File("test-models/new-api/classicNoSearchLex.mzn");
		
		String preferenceModel = "test-models/classicNoSearch.mbr";
		preferenceModelFile = new File(preferenceModel);
		preferenceModelFileLex = new File("test-models/new-api/classicNoSearchLex.mbr");
		preferenceModelFilePareto = new File("test-models/new-api/classicNoSearchPareto.mbr");
		
	}
	
	@Test
	public void testBasic() throws IOException, MiniBrassParseException {
		minibrassRunner.executeBranchAndBound(constraintModelFile, preferenceModelFile, Collections.emptyList());
	}
	
	@Test
	public void testPareto() throws IOException, MiniBrassParseException {
		minibrassRunner.executeBranchAndBound(constraintModelFileForPareto, preferenceModelFilePareto, Collections.emptyList());
	}

	@Test
	public void testLex() throws IOException, MiniBrassParseException {
		minibrassRunner.executeBranchAndBound(constraintModelFileForLex, preferenceModelFileLex, Collections.emptyList());
	}
}

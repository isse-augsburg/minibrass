package isse.mbr.api;

import static org.junit.Assert.*;

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
	private File preferenceModelFile;
	
	@Before
	public void setup () {
		minibrassRunner = new MiniBrassRunner();
		String constraintModel = "test-models/classicNoSearch.mzn";
		constraintModelFile = new File(constraintModel);
		
		String preferenceModel = "test-models/classicNoSearch.mbr";
		preferenceModelFile = new File(preferenceModel);
	}
	
	@Test
	public void test() throws IOException, MiniBrassParseException {
		minibrassRunner.executeBranchAndBound(constraintModelFile, preferenceModelFile, Collections.EMPTY_LIST);
	}

}

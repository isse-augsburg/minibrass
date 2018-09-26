package isse.mbr.api;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import isse.mbr.tools.execution.MiniZincResult;
import isse.mbr.tools.execution.MiniZincRunner;
import isse.mbr.tools.execution.MiniZincSolution;

public class MiniZincRunnerTest {

	private MiniZincRunner runner;

	@Before
	public void setup() {
		runner = new MiniZincRunner();
	}

	@Test
	public void testBasicExecution() {
		String model = "test-models/basicMzn.mzn";
		File modelFile = new File(model);
		MiniZincResult result = runner.solve(modelFile);
		MiniZincSolution lastSolution = result.getLastSolution();
		System.out.println(lastSolution);
	}

}

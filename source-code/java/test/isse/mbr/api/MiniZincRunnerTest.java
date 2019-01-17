package isse.mbr.api;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.tools.execution.MiniZincResult;
import isse.mbr.tools.execution.MiniZincRunner;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincVariable;

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
		
		System.out.println(":::::::::::::::::::::::::::::..");
		System.out.println(lastSolution);
		int expectedA = 0;
		int expectedB = 1;
		int expectedC = 2;
		
		MiniZincVariable variableA = lastSolution.getVariable("a");
		// TODO check if types are correct
		int actualValueA = (Integer) variableA.getValue();
		Assert.assertEquals(expectedA, actualValueA);
		Assert.assertEquals(expectedB, lastSolution.getVariable("b").getValue());
		Assert.assertEquals(expectedC, lastSolution.getVariable("c").getValue());
	}

	


}

package isse.mbr.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.SetType;
import isse.mbr.parsing.DznParser;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassPostProcessor;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincTensor;
import isse.mbr.tools.execution.MiniZincVariable;

public class MiniZincDznParserTest {

	private DznParser dznParser;
	private MiniBrassPostProcessor postProcessor;
	
	@Before
	public void setUp() throws Exception {
		dznParser = new DznParser();
		postProcessor = new MiniBrassPostProcessor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test 
	public void testInt() throws MiniBrassParseException {
		String testDznLine = "A = 2;";
		MiniZincVariable parsedA = dznParser.parseVariable(testDznLine);
		Assert.assertEquals("A", parsedA.getName());
		Assert.assertEquals(parsedA.getValue(), 2);
		Assert.assertTrue( parsedA.getType() instanceof  IntType);
		Assert.assertEquals("2", parsedA.getMznExpression());
		
		testDznLine = "A = 1..3;";
		parsedA = dznParser.parseVariable(testDznLine);
		Assert.assertEquals("A", parsedA.getName());
		Assert.assertTrue (parsedA.getType() instanceof SetType);
		
		Set<Integer> expectedSet = new HashSet<>(Arrays.asList(1,2,3));
		Assert.assertEquals(expectedSet, parsedA.getValue());
		Assert.assertEquals("1..3", parsedA.getMznExpression());
	}
	
	@Test
	public void test2dTensor() throws MiniBrassParseException {
		String testDznLine = "A = array2d(1..3, 1..2, [2, 3, 4, 5, 6, 7]);";
		MiniZincVariable parsedA = dznParser.parseVariable(testDznLine);
		Assert.assertEquals("A", parsedA.getName());

		MiniZincTensor a = (MiniZincTensor) parsedA.getValue();
		int[][] A = new int[3][2];
		A[0][0] = 2;
		A[0][1] = 3;
		A[1][0] = 4;
		A[1][1] = 5;
		A[2][0] = 6;
		A[2][1] = 7;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 2; ++j) {
				MiniZincVariable field = a.get(i + 1, j + 1);
				Assert.assertEquals(A[i][j], field.getValue());
			}
		}
		Assert.assertTrue( parsedA.getType() instanceof  ArrayType);
		Assert.assertEquals("array2d(1..3, 1..2, [2, 3, 4, 5, 6, 7])", parsedA.getMznExpression());
	}

	@Test
	public void testArray() throws MiniBrassParseException {
		String testDznLine = "B = array1d(0..2, [1, 4, 2]);";
		MiniZincVariable parsedB = dznParser.parseVariable(testDznLine);
		Assert.assertEquals("B", parsedB.getName());

		MiniZincTensor b = (MiniZincTensor) parsedB.getValue();
		int[] B = new int[] { 1, 4, 2 };
		for (int i = 0; i < B.length; ++i) {
			MiniZincVariable field = b.get(i);
			Assert.assertEquals(B[i], field.getValue());
		}
		Assert.assertTrue( parsedB.getType() instanceof  ArrayType);
		Assert.assertEquals("array1d(0..2, [1, 4, 2])", parsedB.getMznExpression());
	}
	
	@Test
	public void testMiniBrassPostProcessing() {
		MiniZincSolution solution = new MiniZincSolution();
		solution.processSolutionLine("a = 1;");
		solution.processSolutionLine("b = 3;");
		
		String mznConstraint = "a > sol(a) /\\ b > sol(b)";
		System.out.println(mznConstraint);
		String expectedAfterReplacement = "a > (1) /\\ b > (3)";
		String actualAfterReplacement = postProcessor.processSolution(mznConstraint, solution);
		Assert.assertEquals(expectedAfterReplacement, actualAfterReplacement);
	}

	@Test
	public void testBooleanArray() throws MiniBrassParseException {
		String testDznLine = "mbr_valuations_cr1 = array1d(1..3, [false, false, false]);";
		MiniZincVariable parsedB = dznParser.parseVariable(testDznLine);
		Assert.assertEquals("B", parsedB.getName());

		MiniZincTensor b = (MiniZincTensor) parsedB.getValue();
		int[] B = new int[] { 1, 4, 2 };
		for (int i = 0; i < B.length; ++i) {
			MiniZincVariable field = b.get(i);
			Assert.assertEquals(B[i], field.getValue());
		}
		Assert.assertTrue( parsedB.getType() instanceof  ArrayType);
		Assert.assertEquals("array1d(0..2, [1, 4, 2])", parsedB.getMznExpression());
	}
	
}

package isse.mbr.integration;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.ProductType;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.types.NamedRef;
import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;

public class VotingProductsTest {

	String minibrassModel = "test-models/votePareto.mbr";
	String minibrassLexModel = "test-models/voteLex.mbr";
	String minibrassCompiled = "test-models/votePareto_o.mzn";
	String minizincModel = "test-models/votePareto.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);

        launcher.setMinizincGlobals("jacop");
		launcher.setFlatzincExecutable("fzn-jacop");
	}

	// TODO test case for wrong typing 
	
	@Test 
	public void testVotingPareto() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());

		// The solve item should look like that:
		// ( ref_to_agent1 pareto ( ref_to_agent2 pareto ref_to_agent3 ) )
		MiniBrassAST model = compiler.getUnderlyingParser().getLastModel();
		AbstractPVSInstance solveInstance = model.getSolveInstance();
		CompositePVSInstance expectedSolveItem = new CompositePVSInstance();
		
		ReferencedPVSInstance refAgent1 = new ReferencedPVSInstance();
		refAgent1.setReference("agent1");
		refAgent1.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent1"));
		
		ReferencedPVSInstance refAgent2 = new ReferencedPVSInstance();
		refAgent2.setReference("agent2");
		refAgent2.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent2"));
		
		ReferencedPVSInstance refAgent3 = new ReferencedPVSInstance();
		refAgent3.setReference("agent3");
		refAgent3.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent3"));
		
		expectedSolveItem.setLeftHandSide(refAgent1);
		
		CompositePVSInstance rightHandSide = new CompositePVSInstance();
		rightHandSide.setProductType(ProductType.DIRECT);
		rightHandSide.setLeftHandSide(refAgent2);
		rightHandSide.setRightHandSide(refAgent3);
		
		expectedSolveItem.setRightHandSide(rightHandSide);
		expectedSolveItem.setProductType(ProductType.DIRECT);
		
		String expectedString = expectedSolveItem.toString();
		Assert.assertEquals(expectedString, solveInstance.toString());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);

		// 3. check solution
		Assert.assertTrue(listener.isSolved());
	}

	@Test 
	public void testVotingLex() throws IOException, MiniBrassParseException {
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassLexModel), output);
		Assert.assertTrue(output.exists());

		// The solve item should look like that:
		// ( ref_to_agent1 pareto ( ref_to_agent2 pareto ref_to_agent3 ) )
		MiniBrassAST model = compiler.getUnderlyingParser().getLastModel();
		AbstractPVSInstance solveInstance = model.getSolveInstance();
		CompositePVSInstance expectedSolveItem = new CompositePVSInstance();
		
		ReferencedPVSInstance refAgent1 = new ReferencedPVSInstance();
		refAgent1.setReference("agent1");
		refAgent1.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent1"));
		
		ReferencedPVSInstance refAgent2 = new ReferencedPVSInstance();
		refAgent2.setReference("agent2");
		refAgent2.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent2"));
		
		ReferencedPVSInstance refAgent3 = new ReferencedPVSInstance();
		refAgent3.setReference("agent3");
		refAgent3.setReferencedInstance(new NamedRef<AbstractPVSInstance>("agent3"));
		
		expectedSolveItem.setLeftHandSide(refAgent1);
		
		CompositePVSInstance rightHandSide = new CompositePVSInstance();
		rightHandSide.setProductType(ProductType.LEXICOGRAPHIC);
		rightHandSide.setLeftHandSide(refAgent2);
		rightHandSide.setRightHandSide(refAgent3);
		
		expectedSolveItem.setRightHandSide(rightHandSide);
		expectedSolveItem.setProductType(ProductType.LEXICOGRAPHIC);
		
		String expectedString = expectedSolveItem.toString();
		Assert.assertEquals(expectedString, solveInstance.toString());
		
		// 2. execute minisearch
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniSearchModel(new File(minizincModel), null, 60);

		// 3. check solution
		Assert.assertTrue(listener.isSolved());
	}
}

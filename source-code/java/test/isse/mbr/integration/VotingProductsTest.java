package isse.mbr.integration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import isse.mbr.integration.ExternalMorphismTest.Type;
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

@RunWith(Parameterized.class)
public class VotingProductsTest {

	String minibrassModel = "test-models/votePareto.mbr";
	String minibrassLexModel = "test-models/voteLex.mbr";
	String minibrassCompiled = "test-models/votePareto_o.mzn";
	String minizincModel = "test-models/votePareto.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;

	// parameterized test stuff
	enum Type {TESTVOTINGPARETO, TESTVOTINGLEX};
	@Parameters
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
				{Type.TESTVOTINGPARETO, "jacop", "fzn-jacop"},
				{Type.TESTVOTINGPARETO, "gecode", "fzn-gecode"},
				{Type.TESTVOTINGPARETO, "g12_fd", "flatzinc"},
				{Type.TESTVOTINGPARETO, "chuffed", "fzn-chuffed"},
				{Type.TESTVOTINGLEX, "jacop", "fzn-jacop"},
				{Type.TESTVOTINGLEX, "gecode", "fzn-gecode"},
				{Type.TESTVOTINGLEX, "g12_fd", "flatzinc"},
				{Type.TESTVOTINGLEX, "chuffed", "fzn-chuffed"}
		});
	}

	private Type type;
	private String a, b;

	public VotingProductsTest(Type type, String a, String b){
		this.type = type;
		this.a=a; this.b=b; 
	}
	
	@Before
	public void setUp() throws Exception {
		compiler = new MiniBrassCompiler();
		
		launcher = new MiniZincLauncher();
		launcher.setUseDefault(true);
		launcher.setDebug(true);

        launcher.setMinizincGlobals(a);
		launcher.setFlatzincExecutable(b);
	}

	// TODO test case for wrong typing 
	
	@Test 
	public void testVotingPareto() throws IOException, MiniBrassParseException {
		Assume.assumeTrue(type == Type.TESTVOTINGPARETO);
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
		Assume.assumeTrue(type == Type.TESTVOTINGLEX);
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

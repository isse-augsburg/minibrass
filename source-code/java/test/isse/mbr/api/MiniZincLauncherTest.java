package isse.mbr.api;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.BasicTestListener;
import isse.mbr.tools.MiniZincLauncher;
import isse.mbr.tools.processsources.ClosedMiniZincExecutable;
import isse.mbr.tools.processsources.DefaultMiniZincSource;

public class MiniZincLauncherTest {
	String minibrassModel = "test-models/classicMorphed.mbr";
	String minibrassCompiled = "test-models/classic_o.mzn";
	String minizincModel = "test-models/classic_pure.mzn";
	private MiniBrassCompiler compiler;
	private MiniZincLauncher launcher;
	
	@Before
	public void setUp() throws Exception {
		launcher = new MiniZincLauncher();
		compiler = new MiniBrassCompiler(true);
		compiler.setMinizincOnly(true);
	
	}
	
	@Test
	public void testDefaultWithoutArgs() throws IOException, MiniBrassParseException {
		launcher.setUseDefault(true);
		
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minizinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincModel), Collections.EMPTY_LIST, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
	}
	

	@Test
	public void testDefaultWithJacop() throws IOException, MiniBrassParseException {
		// 0. set solver to jacop
		launcher.setMinizincGlobals(DefaultMiniZincSource.JACOP_GLOBALS);
		launcher.setFlatzincExecutable(DefaultMiniZincSource.JACOP_FZN);
		
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minizinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincModel), Collections.EMPTY_LIST, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
	}
	
	
	@Test
	public void testCustom() throws IOException, MiniBrassParseException {
		// I test with mzn_numberjack, but could be done differently (test can be ignored if numberjack is not installed)
		// 0. set solver to jacop
		launcher.setProcessSource(new ClosedMiniZincExecutable(ClosedMiniZincExecutable.GECODE));
		
		// 1. compile minibrass file
		File output = new File(minibrassCompiled);
		compiler.compile(new File(minibrassModel), output);
		Assert.assertTrue(output.exists());
		
		// 2. execute minizinc
		
		BasicTestListener listener = new BasicTestListener();
		launcher.addMiniZincResultListener(listener);
		launcher.runMiniZincModel(new File(minizincModel), Collections.EMPTY_LIST, 60);
		
		// 3. check solution
		Assert.assertTrue(listener.isSolved());
		Assert.assertEquals("1", listener.getLastSolution().get("x"));
	}
}

package isse.mbr.tools.execution;

import java.io.File;
import java.io.IOException;
import java.util.List;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;

/**
 * The MiniBrass runner is responsible for executing branch-and-bound or other 
 * searches that involve executing several MiniZinc processes
 * @author alexander
 *
 */
public class MiniBrassRunner {
	private MiniZincRunner miniZincRunner;
	private MiniBrassCompiler miniBrassCompiler;
	
	public MiniBrassRunner() {
		miniZincRunner = new MiniZincRunner();
		MiniZincConfiguration config = new MiniZincConfiguration();
		config.setUseAllSolutions(false);
		miniZincRunner.setConfiguration(config);
		miniBrassCompiler = new MiniBrassCompiler();
	}
	
	public void executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles) throws IOException, MiniBrassParseException {
		MiniZincSolution solution;
		miniBrassCompiler.setMinizincOnly(true);
		miniBrassCompiler.compile(miniBrassFile);
		MiniBrassParser parser = miniBrassCompiler.getUnderlyingParser();
		String getBetterConstraint = parser.getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();
		
	
		while( (solution = hasNextSolution(miniZincFile)) != null) {
			// print solution 
			
			// process getBetterConstraint with actual solution
			// add constraint to model
			// solve again
			return;
			
		}
	}

	private MiniZincSolution hasNextSolution(File miniZincFile) {
		MiniZincResult result = miniZincRunner.solve(miniZincFile);
		return result.getLastSolution();
	}
}


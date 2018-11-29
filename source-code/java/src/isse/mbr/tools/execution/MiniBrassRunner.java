package isse.mbr.tools.execution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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
	private boolean writeIntermediateFiles;
	private int modelIndex;
	private File originalMiniZincFile;
	
	public MiniBrassRunner() {
		miniZincRunner = new MiniZincRunner();
		MiniZincConfiguration config = new MiniZincConfiguration();
		config.setUseAllSolutions(false);
		miniZincRunner.setConfiguration(config);
		miniBrassCompiler = new MiniBrassCompiler();
		writeIntermediateFiles = true;
		modelIndex = 0;
	}
	
	public void executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles) throws IOException, MiniBrassParseException {
		MiniZincSolution solution;
		miniBrassCompiler.setMinizincOnly(true);
		miniBrassCompiler.compile(miniBrassFile);
		originalMiniZincFile = miniZincFile;
		MiniBrassParser parser = miniBrassCompiler.getUnderlyingParser();
		String getBetterConstraint = parser.getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();
				
		File workingMiniZincModel = miniZincFile;
		while( (solution = hasNextSolution(workingMiniZincModel)) != null) {
			// print solution 
			System.out.println("Found solution: ");
			System.out.println(solution.getRawDznSolution());
			// process getBetterConstraint with actual solution
			System.out.println("I got the following template constraint: ");
			System.out.println(getBetterConstraint);
			String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, solution) + ";";
			System.out.println(updatedConstraint);
			
			// add constraint to model
			// TODO this is not the best way to do it - we should keep the String in main memory
			workingMiniZincModel = appendConstraint(workingMiniZincModel, updatedConstraint);
			// solve again
		}
		cleanup(workingMiniZincModel);		
	}

	private File appendConstraint(File miniZincFile, String updatedConstraint) throws IOException {
		if(writeIntermediateFiles) {
			String name = miniZincFile.getName();
			String nextName = FilenameUtils.removeExtension(name) + "_" + (modelIndex++) + ".mzn";
			File nextFile = new File(miniZincFile.getParentFile(), nextName); 
			FileUtils.copyFile(miniZincFile, nextFile);
			cleanup(miniZincFile);
			miniZincFile = nextFile;
			
		}
		FileWriter fw = new FileWriter(miniZincFile, true);
		fw.write("\n");
		fw.write(updatedConstraint);
		fw.close();
		return miniZincFile;
	}

	private void cleanup(File miniZincFile) {
		if(!miniZincFile.equals(originalMiniZincFile))
			FileUtils.deleteQuietly(miniZincFile);
	}
	private MiniZincSolution hasNextSolution(File miniZincFile) {
		MiniZincResult result = miniZincRunner.solve(miniZincFile);
		return !result.isInvalidated() && result.isSolved() ? result.getLastSolution() : null;
	}
}


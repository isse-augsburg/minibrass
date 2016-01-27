package de.isse;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class is responsible for actually starting the experiments
 * Loads all problems from ../, combines them with the solvers in solvers.txt
 * @author alexander
 *
 */
public class ExperimentRunner {

	public static void main(String[] args) throws IOException {
		ExperimentRunner runner = new ExperimentRunner();
		runner.run();
	}

	private void run() throws IOException {
		File problemDir = new File("../");
		File workingDir = new File("./");
		String workAbsPath = workingDir.getCanonicalPath(); 
		
		File[] directories = problemDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return !workAbsPath.contains(name) && new File(dir, name).isDirectory();
			}
		}); 
		System.out.println(Arrays.toString(directories));  
	}

}

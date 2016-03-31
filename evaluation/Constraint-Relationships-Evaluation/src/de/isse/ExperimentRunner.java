package de.isse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.Configuration;

import de.isse.conf.ConfigurationProvider;
import de.isse.conf.MiniBrassConfig;

/**
 * This class is responsible for actually starting the experiments
 * Loads all problems from ../, combines them with the solvers in solvers.txt
 * @author alexander
 *
 */
public class ExperimentRunner {

	private File evalDir;
	private File workingDir;
	private File problemDir;

	public static void main(String[] args) throws IOException {
		String propertiesFile = "experiments/experiment.properties";
		if(args.length > 0) {
			propertiesFile = args[0];
		}
		Properties p = new Properties();
		p.load(new FileInputStream(propertiesFile));
		
		ExperimentRunner runner = new ExperimentRunner();
		runner.run(p);
	} 

	private void run(Properties props) throws IOException {
		problemDir = new File("../problems/");
		workingDir = new File("./");
		final String workAbsPath = workingDir.getCanonicalPath(); 
		final Pattern p = Pattern.compile(props.getProperty("problemsRegex", ".*")); // TODO lookup regex handling ... yet again
		
		File[] problemDirectories = problemDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				Matcher m = p.matcher(name);
				
				return !workAbsPath.contains(name) && new File(dir, name).isDirectory() && m.matches();
			}
		}); 
		System.out.println("Testing with problems:");
		System.out.println(Arrays.toString(problemDirectories));  
		for(File d : problemDirectories) {
			System.out.println(d.getName());
		}
		System.out.println("------------");
		ConfigurationProvider<MiniBrassConfig> prov = new ConfigurationProvider<>();
		Collection<MiniBrassConfig> configurations = prov.getConfigurations(new MiniBrassConfig(), props);
		
		evalDir = new File("./eval");
		
		try {
			mainLoop(problemDirectories, configurations);
		} finally {
			// evalDir.deleteOnExit(); // TODO make sure it is deleted!
		}
	}

	/**
	 * Combines problems (with instances), configurations, and solvers
	 * @param problemDirectories
	 * @param configurations
	 */
	private void mainLoop(File[] problemDirectories, Collection<MiniBrassConfig> configurations) {
		for(File problem : problemDirectories) {
			System.out.println("Evaluating problem ... "+problem.getName());
			for(MiniBrassConfig mbConfig : configurations) {
				System.out.println("  with "+mbConfig);
				
				buildModel(problem, mbConfig);
				
				// for solver in solvers: execute(solver)
				
				break; // this is just to inspect only one config (for starters)
			}
		}
	}

	private void buildModel(File problem, MiniBrassConfig mbConfig) {
		// 1) build evaluation-conf.dzn file
		
		// 2) copy pvs and search file
		
	}

}

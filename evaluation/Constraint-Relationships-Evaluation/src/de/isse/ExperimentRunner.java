package de.isse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
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
		File problemDir = new File("../problems/");
		File workingDir = new File("./");
		final String workAbsPath = workingDir.getCanonicalPath(); 
		final Pattern p = Pattern.compile(props.getProperty("problemsRegex", ".*")); // TODO lookup regex handling ... yet again
		
		File[] directories = problemDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				Matcher m = p.matcher(name);
				
				return !workAbsPath.contains(name) && new File(dir, name).isDirectory() && m.matches();
			}
		}); 
		System.out.println(Arrays.toString(directories));  
		for(File d : directories) {
			System.out.println(d.getName());
		}
		System.out.println("------------");
		ConfigurationProvider<MiniBrassConfig> prov = new ConfigurationProvider<>();
		prov.getConfigurations(new MiniBrassConfig(), props);
	}

}

package de.isse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
 * This class is responsible for actually starting the experiments Loads all
 * problems from ../, combines them with the solvers in solvers.txt
 * 
 * @author alexander
 *
 */
public class ExperimentRunner {

	private File evalDir;
	private File resultsDir;
	private File workingDir;
	private File problemDir;

	public static void main(String[] args) throws IOException {
		String propertiesFile = "experiments/experiment.properties";
		if (args.length > 0) {
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
		final Pattern p = Pattern.compile(props.getProperty("problemsRegex", ".*"));
		File[] problemDirectories = problemDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				Matcher m = p.matcher(name);

				return !workAbsPath.contains(name) && new File(dir, name).isDirectory() && m.matches();
			}
		});
		System.out.println("Testing with problems:");
		System.out.println(Arrays.toString(problemDirectories));
		
		System.out.println("------------");
		ConfigurationProvider<MiniBrassConfig> prov = new ConfigurationProvider<>();
		Collection<MiniBrassConfig> configurations = prov.getConfigurations(new MiniBrassConfig(), props);

		evalDir = new File("./eval");
		resultsDir = new File("./results");

		try {
			mainLoop(problemDirectories, configurations);
		} finally {
			// evalDir.deleteOnExit(); // TODO make sure it is deleted!
		}
	}

	/**
	 * Combines problems (with instances), configurations, and solvers
	 * 
	 * @param problemDirectories
	 * @param configurations
	 */
	private void mainLoop(File[] problemDirectories, Collection<MiniBrassConfig> configurations) {
		for (File problem : problemDirectories) {
			copyPlainModel(problem);

			for (MiniBrassConfig mbConfig : configurations) {
				buildModel(problem, mbConfig);
				// for instance in instances
				final Pattern p = Pattern.compile(".*dzn");
				// TODO should be evalDir
				File[] dataFiles = problem.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						Matcher m = p.matcher(name);
						return !name.contains("evaluation-conf") && m.matches();
					}
				});
				System.out.println("Instances: " + Arrays.toString(dataFiles));

				for (File instance : dataFiles) {
					System.out.println("Trying instance ... " + instance.getName());
					// for solver in solvers: execute(solver)

				}

				break; // this is just to inspect only one config (for starters)
						// TODO remove later on
			}
		}

	}

	private void copyPlainModel(File problem) {
		// 1) copy all mzn and dzn files from the source directory
		final Pattern p = Pattern.compile(".*(mzn|dzn)"); // TODO lookup regex
		// handling ... yet again

		File[] modelAndDataFiles = problem.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				Matcher m = p.matcher(name);
				return m.matches();
			}
		});

		for (File plainFile : modelAndDataFiles) {
			// copy plainFile to evalDir TODO look up file copy
		}

		System.out.println(Arrays.toString(modelAndDataFiles));

	}

	private void buildModel(File problem, MiniBrassConfig mbConfig) {

		// 2) build evaluation-conf.dzn file
		StringBuilder sb = new StringBuilder();
		sb.append("mostImportantFirst = " + mbConfig.mostImportantFirst.toString() + ";\n");
		sb.append("propagateRedundant = " + mbConfig.propagateRedundant.toString() + ";\n");
		File confDzn = new File(evalDir, "evaluation-conf.dzn");
		FileWriter fw = null;
		try {
			fw = new FileWriter(confDzn);
			fw.write(sb.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 3) copy pvs and search file
		// depending on settings of mbConfig, copy the appropriate files from
		// src dir TODO lookup copy
	}

}

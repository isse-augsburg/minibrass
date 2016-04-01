package de.isse;

import java.io.File;
import java.io.FileFilter;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import de.isse.conf.ConfigurationProvider;
import de.isse.conf.MiniBrassConfig;
import de.isse.conf.Solver;

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
	private Solver[] solvers;
	private File hookDir;

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
		
		// 1) Read problems
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
		
		// 2) Read solvers 
		String solverString = props.getProperty("solvers");
		String[] solverStrings = solverString.split(",");
		solvers = new Solver[solverStrings.length];
		
		for(int i = 0; i < solverStrings.length;++i) {
			solvers[i] = Solver.valueOf(solverStrings[i].trim());
		}
		
		// 3) Read configurations 
		ConfigurationProvider<MiniBrassConfig> prov = new ConfigurationProvider<>();
		Collection<MiniBrassConfig> configurations = prov.getConfigurations(new MiniBrassConfig(), props);

		evalDir = new File("./eval");
		resultsDir = new File("./results");
		hookDir = new File("../eval-model-hooks");
		
		try {
			if(!evalDir.exists())
				evalDir.mkdir();
			if(!resultsDir.exists())
				resultsDir.mkdir();
			
			mainLoop(problemDirectories, configurations);
		} finally {
			// FileUtils.deleteDirectory(evalDir);
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
				File[] dataFiles = evalDir.listFiles(new FilenameFilter() {

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
					for(Solver s : solvers) {
						System.out.println("   Solved by : "+s);
					}

				}

				break; // this is just to inspect only one config (for starters)
						// TODO remove later on
			}
		}

	}

	private void copyPlainModel(File problem) {
		// 1) copy all mzn and dzn files from the source directory
		
		// delete old evalDir
		try {
			FileUtils.deleteDirectory(evalDir);
			evalDir.mkdir();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	    IOFileFilter dznSuffixFilter = FileFilterUtils.suffixFileFilter(".dzn");
	    IOFileFilter mznSuffixFilter = FileFilterUtils.suffixFileFilter(".mzn");
	    
	    IOFileFilter mznFiles = FileFilterUtils.and(FileFileFilter.FILE, mznSuffixFilter);
	    IOFileFilter dznFiles = FileFilterUtils.and(FileFileFilter.FILE, dznSuffixFilter);
	    
	    // Create a filter for either mzn or dzn files
	    FileFilter filter = FileFilterUtils.or(mznFiles, dznFiles); 
	    
	    // Copy using the filter
	    try {
			FileUtils.copyDirectory(problem, evalDir, filter, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	    File[] modelAndDataFiles = problem.listFiles(filter);
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

		File pvsDir = null;
		File searchDir = null;
		
		if(mbConfig.useSPD) {
			pvsDir = new File(hookDir, "/pvs/spd/"); 
		} else {
			pvsDir = new File(hookDir, "/pvs/tpd/"); 
		}
		
		
		switch(mbConfig.search){
		case BAB_NATIVE:
			searchDir = new File(hookDir, "/search/bab-native/"); 
			break;
		case LNS:
			searchDir = new File(hookDir, "/search/lns/"); 
			break;
		case BAB_NONDOM:
			searchDir = new File(hookDir, "/search/bab-non-dom/"); 
			break;
		case BAB_STRICT:
			searchDir = new File(hookDir, "/search/bab-strict/"); 
			break;
		case BAB_WEIGHTED:
			if (mbConfig.useSPD) {
				pvsDir = new File(hookDir, "/pvs/weighted_spd/"); 
			} else {
				pvsDir = new File(hookDir, "/pvs/weighted_tpd/"); 
			}
			searchDir = new File(hookDir, "/search/bab-strict/"); 
			break;
		default:
		}
		
		File pvsFile = new File(pvsDir,"evaluation-pvs.mzn");
		File searchFile = new File(searchDir,"evaluation-search.mzn");
		File confSpec = new File(hookDir, "evaluation-config.mzn");
		File evalHeur = new File(hookDir, "evaluation-heuristics.mzn");
		
		
		try {
			FileUtils.copyFileToDirectory(pvsFile, evalDir);
			FileUtils.copyFileToDirectory(searchFile, evalDir);
			FileUtils.copyFileToDirectory(confSpec, evalDir);
			FileUtils.copyFileToDirectory(evalHeur, evalDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

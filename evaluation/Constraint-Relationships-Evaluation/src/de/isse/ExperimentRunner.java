package de.isse;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import de.isse.conf.ConfigurationProvider;
import de.isse.conf.MiniBrassConfig;
import de.isse.conf.SearchType;
import de.isse.conf.Solver;
import de.isse.jobs.Job;
import de.isse.jobs.JobResult;
import de.isse.results.MiniBrassResult;
import de.isse.time.BookkeepingTimer;

/**
 * This class is responsible for actually starting the experiments Loads all
 * problems from ../, combines them with the solvers in solvers.txt
 * 
 * @author alexander
 *
 */
public class ExperimentRunner {

	private File evalDir;
	private File zipModelDir;
	private File resultsDir;
	private File workingDir;
	private File problemDir; // base dir for all problems
	private Solver[] solvers;
	private File hookDir; // the directory where all search-specific model parts
							// are stored

	private final static String EVAL_CONF_FILE = "evaluation-conf.dzn";
	private final static int JOB_TIME_IDENT = 0; // identifier for timer

	private static boolean EXPORT_MODELS_ZIP = true;
	private static boolean FORCE_OVERRIDE = false;
	
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

		for (int i = 0; i < solverStrings.length; ++i) {
			solvers[i] = Solver.valueOf(solverStrings[i].trim());
		}

		// 3) Read configurations
		ConfigurationProvider<MiniBrassConfig> prov = new ConfigurationProvider<>();
		Collection<MiniBrassConfig> configurations = prov.getConfigurations(new MiniBrassConfig(), props);

		
		evalDir = new File("./eval");
		resultsDir = new File("./results");
		zipModelDir = new File(resultsDir, "zipModels");
		// should models be exported?
		EXPORT_MODELS_ZIP  = Boolean.parseBoolean(props.getProperty("exportModels", "false"));
		hookDir = new File("../eval-model-hooks");

		try {
			if (!evalDir.exists())
				evalDir.mkdir();
			if (!resultsDir.exists())
				resultsDir.mkdir();
			if (!zipModelDir.exists())
				zipModelDir.mkdir();

			mainLoop(problemDirectories, configurations);
		} finally {
			//FileUtils.deleteDirectory(evalDir);
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
				final Pattern p = Pattern.compile(".*\\.dzn");
				File[] dataFiles = evalDir.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						Matcher m = p.matcher(name);
						return !name.contains(EVAL_CONF_FILE) && m.matches();
					}
				});
				System.out.println("Instances: " + Arrays.toString(dataFiles));

				for (File instance : dataFiles) {
					// for solver in solvers: execute(solver)
					for (Solver s : solvers) {
						if(jobExecutable(mbConfig,s)) {
							executeJob(problem, instance, s, mbConfig);							
						}
					}
				}

				//break; // this is just to inspect only one config (for starters)
						// TODO remove later on
			}
		}

	}


	private boolean jobExecutable(MiniBrassConfig mbConfig, Solver s) {
		if(s == Solver.CHOCO || s == Solver.NUMBERJACK || s == Solver.OR_TOOLS || s == Solver.G12) {
			if(mbConfig.search != SearchType.BAB_NATIVE)
				return false; // at the moment, this only works with native search
		}
		return true;
	}

	private void executeJob(File problem, File instance, Solver s, MiniBrassConfig mbConfig) {
		Job evalJob = new Job(problem, instance, s, mbConfig);
		String jobFileName = evalJob.toFileName() + ".ser";
		File jobFile = new File(resultsDir, jobFileName);

		System.out.println(jobFileName);
		if (jobFile.exists() && !FORCE_OVERRIDE) {
			JobResult resJob = null;
			Job writtenJob = null;
			FileInputStream fis = null;
			ObjectInputStream ois = null;

			try {
				fis = new FileInputStream(jobFile);
				ois = new ObjectInputStream(fis);
				resJob = (JobResult) ois.readObject();
				writtenJob = resJob.getJob();
			} catch (Exception e) {
				e.printStackTrace();
				// failed to properly read serialized object - better start job
				// again
			} finally {
				if (fis != null || ois != null)
					try {
						ois.close();
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			if (writtenJob != null) {
				if (evalJob.equals(writtenJob)) {
					System.out.println("Well, we already performed that job so skip it ... ");
					System.out.println("Last time: " + resJob.getResult());
					return;
				}
			}
		}

		// perform actual work ...
		System.out.println("Seems like I gotta take this one ... ");
		MiniBrassResult result = runJob(evalJob, instance);

		// serialize job afterwards
		JobResult jobRes = new JobResult();
		jobRes.setJob(evalJob);
		jobRes.setResult(result);

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(jobFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(jobRes); 
			oos.close();
			fos.close();
			
			// also write the normal text output for quick inspection
			String resultTxt = evalJob.toFileName() + ".txt";
			FileWriter fw = new FileWriter(new File(resultsDir, resultTxt));
			fw.write(evalJob.config.toString());
			fw.write("\n-------------------------\n");
			fw.write(jobRes.getResult().toString());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MiniBrassResult runJob(Job evalJob, File instanceFile) {
		File modelFile = new File(evalDir, evalJob.problem + ".mzn");
		File confFile = new File(evalDir, EVAL_CONF_FILE);

		String flatzincExecutable = evalJob.solver.getFlatzincExec();
		String minizincGlobals = evalJob.solver.getMznGlobals();
		// prepare result object
		int timeoutInMillisecs = evalJob.config.timeout;

		// Numberjack expects timeout in seconds but as int
		long timeOutInSeconds = Math.round(timeoutInMillisecs / 1000.0);
		
		String underlyingCommand = "minisearch --solver " + flatzincExecutable + " -G" + minizincGlobals + " "
				+ modelFile.getPath() + " " + instanceFile.getPath() + " " + confFile.getPath();

		if(evalJob.config.search == SearchType.BAB_NATIVE) { // when using native search, resort to minizinc for the -a flag TODO make numberjack more tolerant regarding that
			underlyingCommand = "minizinc "+ (evalJob.solver == Solver.NUMBERJACK ? "": "-a") +" -f " + flatzincExecutable + " -G" + minizincGlobals + " "
					+ modelFile.getPath() + " " + instanceFile.getPath() + " " + confFile.getPath() ;
			if(evalJob.solver == Solver.NUMBERJACK) { // Numberjack, unfortunately, needs some special treatment
				underlyingCommand = "mzn_numberjack " + modelFile.getPath() + " " + instanceFile.getPath() + " " + confFile.getPath() + " -t "+timeOutInSeconds;
					
			} 
		}
		ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-l", "-c", underlyingCommand);

		System.out.println("About to start: " + pb.command());

		File log = new File("log");
		if (log.exists())
			log.delete();

		pb.redirectErrorStream(true);

		pb.redirectOutput(Redirect.to(log));
		pb.environment().put("TIMELIMIT", Integer.toString(timeoutInMillisecs));
		// pb.environment().put("JCP",
		// "/home/alexander/Documents/Projects/git-new/practice/jacop/target/classes");
		final Process p;
		BookkeepingTimer t = new BookkeepingTimer();

		
		MiniBrassResult result = new MiniBrassResult();
		try {
			p = pb.start();
			t.tick(JOB_TIME_IDENT);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					System.out.println("Destroyed by timeout ... ");
					p.destroy();
				}
			}, timeoutInMillisecs);

			p.waitFor();
			t.tock(JOB_TIME_IDENT);
			timer.cancel();

			assert pb.redirectInput() == Redirect.PIPE;
			assert pb.redirectOutput().file() == log;
			assert p.getInputStream().read() == -1;

			// compile results object
			processResult(result, log, t);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Completed job; Result: " + result);

		// zip model file for debug purposes TODO for now only copy directory, not really zipping
		if (EXPORT_MODELS_ZIP) {
			exportZippedModel(evalJob, pb);
		}
		return result;
	}

	private void exportZippedModel(Job evalJob, ProcessBuilder pb) {
		String fileName = evalJob.toFileName();
		File exportDir = new File(zipModelDir, fileName);
		try {
			FileUtils.copyDirectory(evalDir, exportDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> command = pb.command();
		StringBuilder commandBuilder = new StringBuilder("#!/bin/bash\n");
		for(String c : command)  {
			commandBuilder.append(c);
			commandBuilder.append(" ");
		}
		File commandFile = new File(exportDir, "command.sh");
		FileWriter fw = null;
		try {
			fw = new FileWriter(commandFile);
			fw.write(commandBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}

	/**
	 * Reads the output of a minisearch execution
	 * 
	 * @param result
	 * @param log
	 * @param t
	 */
	private void processResult(MiniBrassResult result, File log, BookkeepingTimer t) {
		Scanner sc = null;
		final String optimalitySep = "==========";
		final String solutionSep = "----------";

		try {
			sc = new Scanner(log);

			int noSolutions = 0; // how many solutions did we actually see
									// during optimization
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("penSum =")) {
					Scanner miniScan = new Scanner(line);
					miniScan.next();
					miniScan.next();
					int nextObj = miniScan.nextInt();
					result.objective = Math.min(result.objective, nextObj);
					result.solved = true;

					miniScan.close();
				}

				if (line.contains(optimalitySep)) {
					result.solvedOptimally = true;
				}

				if (line.contains(solutionSep)) {
					++noSolutions;
				}
			}
			result.noSolutions = noSolutions;
			result.elapsedSeconds = t.getElapsedSecs(JOB_TIME_IDENT);
			result.valid = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result.valid = false;
		} finally {
			if (sc != null)
				sc.close();
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

		File confDzn = new File(evalDir, EVAL_CONF_FILE);
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

		if (mbConfig.useSPD) {
			pvsDir = new File(hookDir, "/pvs/spd/");
		} else {
			pvsDir = new File(hookDir, "/pvs/tpd/");
		}

		switch (mbConfig.search) {
		case BAB_NATIVE:
			if (mbConfig.useSPD) {
				pvsDir = new File(hookDir, "/pvs/spd-native/");
			} else {
				pvsDir = new File(hookDir, "/pvs/tpd-native/");
			}

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

		File pvsFile = new File(pvsDir, "evaluation-pvs.mzn");
		File searchFile = new File(searchDir, "evaluation-search.mzn");
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

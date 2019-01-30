package isse.mbr.tools.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassCompiler.StdoutConsoleHandler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;

/**
 * The MiniBrass runner is responsible for executing branch-and-bound or other
 * searches that involve executing several MiniZinc processes
 * 
 * usage: minibrass constraintModel.mzn file.mbr [dataFiles.dzn]
 * 
 * @author alexander
 *
 */
public class MiniBrassRunner {
	private MiniZincRunner miniZincRunner;
	private MiniBrassCompiler miniBrassCompiler;
	private boolean writeIntermediateFiles;
	private boolean debug;
	private int modelIndex;
	private File originalMiniZincFile;
	private List<MiniZincSolution> allSolutions;
	private HelpFormatter formatter;
	private Options options;
	private static Logger logger;
	
	public MiniBrassRunner() {
		miniZincRunner = new MiniZincRunner();
		MiniZincConfiguration config = new MiniZincConfiguration();
		config.setUseAllSolutions(true);

		miniZincRunner.setConfiguration(config);
		miniBrassCompiler = new MiniBrassCompiler();
		writeIntermediateFiles = true;
		debug = false;
		modelIndex = 0;
	}

	public MiniBrassRunner(MiniZincConfiguration configuration) {
		this();
		miniZincRunner.setConfiguration(configuration);
	}

	public MiniZincSolution executeBranchAndBound(File miniZincFile, File miniBrassFile, List<File> dataFiles)
			throws IOException, MiniBrassParseException {
		MiniZincSolution solution;
		MiniZincSolution lastSolution = null;
		
		allSolutions = new LinkedList<>();
		miniBrassCompiler.setMinizincOnly(true);
		String compiledMiniBrassCode = miniBrassCompiler.compileInMemory(miniBrassFile);
		originalMiniZincFile = miniZincFile;
		MiniBrassParser parser = miniBrassCompiler.getUnderlyingParser();
		String getBetterConstraint = parser.getLastModel().getDereferencedSolveInstance().getGeneratedBetterPredicate();
		MiniBrassPostProcessor postProcessor = new MiniBrassPostProcessor();

		File workingMiniZincModel = appendMiniZincCode(miniZincFile, compiledMiniBrassCode);

		while ((solution = hasNextSolution(workingMiniZincModel, dataFiles)) != null) {
			// append solution
			allSolutions.add(solution);
			lastSolution = solution;
			
			// print solution
			System.out.println("Found solution: ");
			System.out.println(solution.getRawDznSolution());
			// process getBetterConstraint with actual solution
			System.out.println("I got the following template constraint: ");
			System.out.println(getBetterConstraint);
			String updatedConstraint = "constraint " + postProcessor.processSolution(getBetterConstraint, solution)
					+ ";";
			System.out.println(updatedConstraint);

			// add constraint to model
			// TODO this is not the best way to do it - we should keep the String in main
			// memory
			workingMiniZincModel = appendMiniZincCode(workingMiniZincModel, updatedConstraint);
			// solve again
		}
		cleanup(workingMiniZincModel);
		return lastSolution;
	}

	public static void main(String[] args) throws SecurityException, IOException {
		logger = Logger.getGlobal();
		logger.setLevel(Level.FINER);
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

		SimpleFormatter formatterTxt = new SimpleFormatter();
		FileHandler logFile = new FileHandler("log2.txt");
		logFile.setLevel(Level.FINER);

		logFile.setFormatter(formatterTxt);
		logger.addHandler(logFile);

		// Create and set handler
		Handler systemOut = new StdoutConsoleHandler();
		systemOut.setLevel(Level.ALL);

		logger.addHandler(systemOut);
		logger.setLevel(Level.SEVERE);

		// Prevent logs from processed by default Console handler.
		logger.setUseParentHandlers(false); // Solution 1

		new MiniBrassRunner().doMain(args);
	}
	
	private void printUsage() {
		formatter.printHelp("minibrass [<options>] <minibrass-model>.mbr <minizinc-model>.mzn <minizinc-data>.dzn\n\nOptions:\n", options);
	}
	
	private void doMain(String[] args) {
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption("d", "debug", false, "write intermediate files");
		options.addOption("s", "solver", true, "solver to use for branch-and-bound");
		options.addOption("t", "timeout", true, "timeout in milliseconds");
		
		formatter = new HelpFormatter();
		String minibrassFile = null;
		String minizincModelFile = null;
		String minizincDataFile = null;
		List<File> dataFiles = new LinkedList<>();
		
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			List<String> argList = line.getArgList();
			
			if (line.hasOption('h')) {
				printUsage();
				System.exit(0);
			}
			
			if (line.hasOption("solver")) {
				miniZincRunner.getConfiguration().setSolverId(line.getOptionValue("solver"));
			}

			if (line.hasOption("timeout")) {
				miniZincRunner.getConfiguration().setTimeout(Integer.parseInt(line.getOptionValue("timeout")));
			}

			if (argList.size() < 2) {
				System.out.println("minibrass expects one MiniBrass file, one MiniZinc file and optionally some data files as input.");
				printUsage();
				System.exit(1);
			} else {
				minibrassFile = argList.get(0);
				if (!minibrassFile.endsWith("mbr")) {
					System.out.println("Warning: MiniBrass file ending on .mbr expected!");
				}
				
				minizincModelFile = argList.get(1);
				if (!minizincModelFile.endsWith("mzn")) {
					System.out.println("Warning: MiniZinc file ending on .mzn expected!");
				}
				
				if(argList.size() > 2) {
					minizincDataFile = argList.get(2);
					if (!minizincDataFile.endsWith("dzn")) {
						System.out.println("Warning: MiniZinc data file ending on .dzn expected!");
					}
					dataFiles.add(new File(minizincDataFile));
				}
			}

			if (line.hasOption("debug")) {
				debug = true;				
			} 

			logger.info("Processing " + minibrassFile + " | " + minizincModelFile+ " | " + minizincDataFile +" to file.");
			executeBranchAndBound(new File(minizincModelFile), new File(minibrassFile), dataFiles);
			
		} catch (ParseException exp) {
			logger.severe("Unexpected exception:" + exp.getMessage());
			printUsage();
		} catch (FileNotFoundException e) {
			logger.severe("File was not found");
			e.printStackTrace();
		} catch (MiniBrassParseException e) {
			logger.severe("Could not parse MiniBrass model:");
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe("IO error: ");
			e.printStackTrace();
		}
		
	}
	
	private File appendMiniZincCode(File miniZincFile, String updatedConstraint) throws IOException {
		if (writeIntermediateFiles) {
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
		if (!miniZincFile.equals(originalMiniZincFile) && !debug)
			FileUtils.deleteQuietly(miniZincFile);
	}

	private MiniZincSolution hasNextSolution(File miniZincFile, List<File> dataFiles) {
		MiniZincResult result = miniZincRunner.solve(miniZincFile, dataFiles, -1);
		return !result.isInvalidated() && result.isSolved() ? result.getLastSolution() : null;
	}

	public MiniZincConfiguration getMiniZincRunnerConfiguration() {
		return miniZincRunner.getConfiguration();
	}

	public void setMiniZincConfiguration(MiniZincConfiguration configuration) {
		miniZincRunner.setConfiguration(configuration);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public List<MiniZincSolution> getAllSolutions() {
		return allSolutions;
	}

	public void setAllSolutions(List<MiniZincSolution> allSolutions) {
		this.allSolutions = allSolutions;
	}

}

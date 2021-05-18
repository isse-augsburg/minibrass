package isse.mbr.tools.execution;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Main function for executing the {@link MiniBrassRunner}.
 *
 * @see MiniBrassRunner
 */
public final class MiniBrassRunnerMain {
	private final Logger logger = setupLogger();
	private final Options options = setupOptions();

	private MiniBrassRunnerMain() throws IOException { }

	public static void main(String[] args) throws IOException {
		new MiniBrassRunnerMain().run(args);
	}

	private void run(String[] args) {
		File miniBrassFile = null;
		File miniZincFile = null;
		List<File> dataFiles = new LinkedList<>();
		MiniBrassRunner miniBrassRunner = new MiniBrassRunner();

		try {
			// parse the command line arguments
			CommandLine line = new DefaultParser().parse(options, args);

			List<String> argList = line.getArgList();

			if (line.hasOption('h')) {
				printUsage();
				System.exit(0);
			}

			if (line.hasOption("solver")) {
				miniBrassRunner.getMiniZincRunnerConfiguration().setSolverId(line.getOptionValue("solver"));
			}

			if (line.hasOption("timeout")) {
				miniBrassRunner.getMiniZincRunnerConfiguration().setTimeout(Integer.parseInt(line.getOptionValue("timeout")));
			}

			if (line.hasOption("random-seed")) {
				miniBrassRunner.setInitialRandomSeed(Integer.parseInt(line.getOptionValue("random-seed")));
			}

			if (argList.size() < 2) {
				System.out.println(
						"minibrass expects one MiniBrass file, one MiniZinc file and optionally some data files as input.");
				printUsage();
				System.exit(1);
			} else {
				String miniBrassFileName = argList.get(0);
				if (!miniBrassFileName.endsWith("mbr")) {
					System.out.println("Warning: MiniBrass file ending on .mbr expected!");
				}
				miniBrassFile = new File(miniBrassFileName);

				String miniZincFileName = argList.get(1);
				if (!miniZincFileName.endsWith("mzn")) {
					System.out.println("Warning: MiniZinc file ending on .mzn expected!");
				}
				miniZincFile = new File(miniZincFileName);

				if (argList.size() > 2) {
					String minizincDataFileName = argList.get(2);
					if (!minizincDataFileName.endsWith("dzn")) {
						System.out.println("Warning: MiniZinc data file ending on .dzn expected!");
					}
					dataFiles.add(new File(minizincDataFileName));
				}
			}

			miniBrassRunner.setDebug(line.hasOption("debug"));
			miniBrassRunner.getMiniZincRunnerConfiguration().setUseAllSolutions(line.hasOption("pareto"));

			logger.info(
					String.format("Processing %s | %s | %s to file.", miniBrassFile.getName(), miniZincFile.getName(),
							dataFiles.stream().map(File::getName).collect(Collectors.joining(" / "))));
			Collection<MiniZincSolution> solutions = miniBrassRunner.executeBranchAndBound(miniZincFile, miniBrassFile,
					dataFiles);
			System.out.println(solutions.stream()
					.map(MiniZincSolution::getRawDznSolution)
					.collect(Collectors.joining("\n-----------\n")));
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

	private static Logger setupLogger() throws IOException {
		Logger logger = Logger.getGlobal();
		logger.setLevel(Level.FINER);
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

		SimpleFormatter formatterTxt = new SimpleFormatter();
		FileHandler logFile = new FileHandler("log.txt");
		logFile.setLevel(Level.FINER);

		logFile.setFormatter(formatterTxt);
		logger.addHandler(logFile);

		// Create and set handler
		Handler systemOut = new MiniBrassCompiler.StdoutConsoleHandler();
		systemOut.setLevel(Level.ALL);

		logger.addHandler(systemOut);
		logger.setLevel(Level.SEVERE);

		// Prevent logs from processed by default Console handler.
		logger.setUseParentHandlers(false);

		return logger;
	}

	private static Options setupOptions() {
		return new Options()
				.addOption("h", "help", false, "print this message")
				.addOption("d", "debug", false, "write intermediate files")
				.addOption("r", "random-seed", true, "initial random seed for branch-and-bound")
				.addOption("s", "solver", true, "solver to use for branch-and-bound")
				.addOption("t", "timeout", true, "timeout in milliseconds")
				.addOption("p", "pareto", false, "find all pareto-optimal solutions");
	}

	private void printUsage() {
		new HelpFormatter().printHelp(
				"minibrass [<options>] <minibrass-model>.mbr <minizinc-model>.mzn <minizinc-data>.dzn\n\nOptions:\n",
				options);
	}
}

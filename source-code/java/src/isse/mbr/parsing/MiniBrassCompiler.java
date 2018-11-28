package isse.mbr.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.ConsoleHandler;
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

import isse.mbr.model.MiniBrassAST;

/**
 * The main entry point for the MiniBrass compiler that converts MiniBrass
 * source code files into MiniZinc
 * 
 * usage: mbr2mzn [-o output] [-m] [-s] file.mbr
 * 
 * Default output is "file_o.mzn"
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassCompiler {

	private final static Logger LOGGER = Logger.getGlobal();

	private boolean minizincOnly; // does not generate anything that is related
									// to MiniSearch (i.e. annotations for
									// getBetter-predicates)
	private boolean genHeuristics;
	private File out = null;
	private String minibrassFile;

	// this should not be set by flag - rather move the output from MiniZinc to
	// MiniBrass file
	private boolean suppressOutput = false;

	private String externalMiniBrassStdDirPath;
	private MiniBrassParser underlyingParser; // required for further
												// post-processing as in, e.g.,
												// pairwise comparison

	// CLI business
	private Options options;
	private HelpFormatter formatter;

	private CodeGenerator underlyingCodegen;

	public MiniBrassCompiler() {
	}

	public MiniBrassCompiler(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	public static class StdoutConsoleHandler extends ConsoleHandler {
		@Override
		protected void setOutputStream(OutputStream out) throws SecurityException {
			super.setOutputStream(System.out);
		}
	}

	public static void main(String[] args) throws SecurityException, IOException {
		Logger logger = Logger.getGlobal();
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

		new MiniBrassCompiler().doMain(args);
	}

	public String compile(File input) throws IOException, MiniBrassParseException {
		String inputPath = input.getAbsolutePath();
		String mbrFilePrefix = inputPath.substring(0, inputPath.lastIndexOf('.'));
		File defaultOutput = new File(mbrFilePrefix + "_o.mzn");
		String generatedCode = compile(input, defaultOutput);
		return generatedCode;
	}

	public String compile(File input, File output) throws IOException, MiniBrassParseException {
		FileInputStream fileInputStream = new FileInputStream(input);

		String generatedCode = compile(fileInputStream);
		System.out.println("MiniBrass code compiled successfully to " + output + ".");
		// write code to file
		FileWriter fw = new FileWriter(output);
		fw.write(generatedCode);
		fw.close();
		return generatedCode;
	}

	public String compile(InputStream input) throws MiniBrassParseException {
		underlyingParser = new MiniBrassParser();
		underlyingParser.setExternalMiniBrassStdDirPath(getExternalMiniBrassStdDirPath());
		MiniBrassAST model = underlyingParser.parse(input);

		underlyingCodegen = new CodeGenerator();
		underlyingCodegen.setOnlyMiniZinc(isMinizincOnly());
		underlyingCodegen.setGenHeuristics(isGenHeuristics());
		underlyingCodegen.setSuppressOutputGeneration(suppressOutput);

		// make sure there is one solve item !
		if (model.getSolveInstance() == null) {
			throw new MiniBrassParseException("Model contains no solve item! Please add one");
		}

		String generatedCode = underlyingCodegen.generateCode(model);
		return generatedCode;
	}

	private void printUsage() {
		formatter.printHelp("mbr2mzn [<options>] <minibrass-model>.mbr\n\nOptions:\n", options);
	}

	public void doMain(String[] args) {

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption("s", "generate-heuristics", false,
				"generate heuristics for search (can lead to long flatzinc compilation)");
		options.addOption("m", "only-minizinc", false,
				"do not generate MiniSearch predicates but only MiniZinc code (top level PVS must be int)");
		options.addOption("o", "output", true, "output compiled MiniZinc to this file");
		// options.addOption("", "input-from-stdin", false, "Read input from standard
		// input");

		formatter = new HelpFormatter();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			List<String> argList = line.getArgList();

			if (line.hasOption('h')) {
				printUsage();
				System.exit(0);
			}

			if (argList.size() != 1) {
				System.out.println("mbr2mzn expects exactly one MiniBrass file as input.");
				printUsage();
				System.exit(1);
			} else {
				minibrassFile = argList.get(0);
				if (!minibrassFile.endsWith("mbr")) {
					System.out.println("Warning: MiniBrass file ending on .mbr expected!");
				}
			}

			if (line.hasOption("output")) {
				out = new File(line.getOptionValue("output"));
			} else {
				String mbrFilePrefix = minibrassFile.substring(0, minibrassFile.lastIndexOf('.'));
				out = new File(mbrFilePrefix + "_o.mzn");
			}

			if (line.hasOption("only-minizinc")) {
				LOGGER.info("Only generating MiniZinc (not MiniSearch) code");
				minizincOnly = true;
			}

			if (line.hasOption("generate-heuristics")) {
				LOGGER.info("Generate search heuristics as well");
				genHeuristics = true;
			}

			LOGGER.info("Processing " + minibrassFile + " to file " + out);
			File mbrFile = new File(minibrassFile);

			compile(mbrFile, out);
		} catch (ParseException exp) {
			LOGGER.severe("Unexpected exception:" + exp.getMessage());
			printUsage();
		} catch (FileNotFoundException e) {
			LOGGER.severe("File " + minibrassFile + " was not found");
			e.printStackTrace();
		} catch (MiniBrassParseException e) {
			LOGGER.severe("Could not parse MiniBrass model:");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IO error: ");
			e.printStackTrace();
		}
	}

	public boolean isMinizincOnly() {
		return minizincOnly;
	}

	public void setMinizincOnly(boolean minizincOnly) {
		this.minizincOnly = minizincOnly;
	}

	public boolean isGenHeuristics() {
		return genHeuristics;
	}

	public void setGenHeuristics(boolean genHeuristics) {
		this.genHeuristics = genHeuristics;
	}

	public MiniBrassParser getUnderlyingParser() {
		return underlyingParser;
	}

	public void setUnderlyingParser(MiniBrassParser underlyingParser) {
		this.underlyingParser = underlyingParser;
	}

	public CodeGenerator getUnderlyingCodegen() {
		return underlyingCodegen;
	}

	public void setUnderlyingCodegen(CodeGenerator underlyingCodegen) {
		this.underlyingCodegen = underlyingCodegen;
	}

	public String getExternalMiniBrassStdDirPath() {
		return externalMiniBrassStdDirPath;
	}

	public void setExternalMiniBrassStdDirPath(String externalMiniBrassStdDirPath) {
		this.externalMiniBrassStdDirPath = externalMiniBrassStdDirPath;
	}

}

package isse.mbr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

import isse.mbr.model.MiniBrassAST;

/**
 * The main entry point for the MiniBrass compiler that converts MiniBrass
 * source code files into MiniZinc
 * 
 * usage: mbr2mzn [-o output] file.mbr
 * 
 * Default output is "file_o.mzn"
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassCompiler {

	private final static Logger LOGGER = Logger.getGlobal();

	@Option(name = "-o", usage = "output compiled MiniZinc to this file", metaVar = "MZN-OUTPUT")
	private File out = null;

	@Argument(required = true, metaVar = "MBR-FILE")
	private String minibrassFile;

	public static class StdoutConsoleHandler extends ConsoleHandler {
		protected void setOutputStream(OutputStream out) throws SecurityException {
			super.setOutputStream(System.out);
		}
	}

	public static void main(String[] args) throws SecurityException, IOException {
		Logger logger = Logger.getGlobal();
		logger.setLevel(Level.FINER);
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
		
		SimpleFormatter formatterTxt = new SimpleFormatter();
		FileHandler logFile = new FileHandler("log.txt");
		logFile.setLevel(Level.FINER);

		logFile.setFormatter(formatterTxt);
		logger.addHandler(logFile);

		// Create and set handler
		Handler systemOut = new StdoutConsoleHandler();
		systemOut.setLevel(Level.ALL);

		logger.addHandler(systemOut);
		logger.setLevel(Level.ALL);

		// Prevent logs from processed by default Console handler.
		logger.setUseParentHandlers(false); // Solution 1

		new MiniBrassCompiler().doMain(args);
	}

	private void doMain(String[] args) {
		CmdLineParser cmdLineParser = new CmdLineParser(this);
		try {
			cmdLineParser.parseArgument(args);
			if (!minibrassFile.endsWith("mbr")) {
				System.out.println("Warning: MiniBrass file ending on .mbr expected!");
			}
			if (out == null) {
				String mbrFilePrefix = minibrassFile.substring(0, minibrassFile.lastIndexOf('.'));
				out = new File(mbrFilePrefix + "_o.mzn");
			}
			LOGGER.info("Processing " + minibrassFile + " to file " + out);
			File mbrFile = new File(minibrassFile);

			MiniBrassParser parser = new MiniBrassParser();
			MiniBrassAST model = parser.parse(mbrFile);
			CodeGenerator codegen = new CodeGenerator();
			String generatedCode = codegen.generateCode(model);

			// write code to file
			FileWriter fw = new FileWriter(out);
			fw.write(generatedCode);
			fw.close();
		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			LOGGER.severe(e.getMessage());
			LOGGER.severe("mbr2mzn [options...] MBR-FILE");
			// print the list of available options
			cmdLineParser.printUsage(System.err);
			LOGGER.severe("\n");

			// print option sample. This is useful some time
			LOGGER.severe("  Example: mbr2mzn" + cmdLineParser.printExample(OptionHandlerFilter.ALL) + " MBR-FILE");

			return;
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

}

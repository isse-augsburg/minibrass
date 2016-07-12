package isse.mbr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

import isse.mbr.model.MiniBrassAST;

/**
 * The main entry point for the MiniBrass compiler
 * that converts MiniBrass source code files into MiniZinc
 * 
 * usage: mbr2mzn [-o output] file.mbr
 * 
 * Default output is "file_o.mzn"
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassCompiler {

	@Option(name="-o",usage="output compiled MiniZinc to this file",metaVar="MZN-OUTPUT")
	private File out = null;
	
	@Argument(required=true,metaVar="MBR-FILE")
	private String minibrassFile;
	
	public static void main(String[] args) {
		new MiniBrassCompiler().doMain(args);
	}

	private void doMain(String[] args) {
		CmdLineParser cmdLineParser = new CmdLineParser(this);
		try {
			cmdLineParser.parseArgument(args);
			if(!minibrassFile.endsWith("mbr")) {
				System.out.println("Warning: MiniBrass file ending on .mbr expected!");
			}
			if(out == null) {
				String mbrFilePrefix = minibrassFile.substring(0, minibrassFile.lastIndexOf('.'));
				out = new File(mbrFilePrefix+"_o.mzn");
			}
			System.out.println("Processing "+minibrassFile + " to file "+out);
			File mbrFile = new File(minibrassFile);
			
			MiniBrassParser parser = new MiniBrassParser(); 
			MiniBrassAST model = parser.parse(mbrFile);
			CodeGenerator codegen = new CodeGenerator();
			String generatedCode = codegen.generateCode(model);
			
			// write code to file 
			FileWriter fw = new FileWriter(out);
			fw.write(generatedCode);
			fw.close();
		} catch( CmdLineException e ) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("mbr2mzn [options...] MBR-FILE");
            // print the list of available options
            cmdLineParser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: mbr2mzn"+cmdLineParser.printExample(OptionHandlerFilter.ALL)+" MBR-FILE");

            return;
        } catch (FileNotFoundException e) {
        	System.err.println("File "+minibrassFile+ " was not found");
			e.printStackTrace();
		} catch (MiniBrassParseException e) {
			System.err.println("Could not parse MiniBrass model:");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error: ");
			e.printStackTrace();
		}
	}

}

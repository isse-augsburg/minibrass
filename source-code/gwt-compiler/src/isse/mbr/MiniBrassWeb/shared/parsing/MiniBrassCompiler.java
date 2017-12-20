package isse.mbr.MiniBrassWeb.shared.parsing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import isse.mbr.MiniBrassWeb.shared.model.MiniBrassAST;

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

	private boolean minizincOnly; // does not generate anything that is related
									// to MiniSearch (i.e. annotations for
									// getBetter-predicates)
	private boolean genHeuristics;

	// this should not be set by flag - rather move the output from MiniZinc to
	// MiniBrass file
	private boolean suppressOutput = false;

	private MiniBrassParser underlyingParser; // required for further
												// post-processing as in, e.g.,
												// pairwise comparison

	public MiniBrassCompiler() {
	}

	public MiniBrassCompiler(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	public String compile(String model) throws MiniBrassParseException {
		return compile(new ByteArrayInputStream(model.getBytes()));
	}

	public String compile(InputStream input) throws MiniBrassParseException {
		underlyingParser = new MiniBrassParser();
		MiniBrassAST model = underlyingParser.parse(input);

		CodeGenerator codegen = new CodeGenerator();
		codegen.setOnlyMiniZinc(isMinizincOnly());
		codegen.setGenHeuristics(isGenHeuristics());
		codegen.setSuppressOutputGeneration(suppressOutput);

		// make sure there is one solve item !
		if (model.getSolveInstance() == null) {
			throw new MiniBrassParseException("Model contains no solve item! Please add one");
		}

		String generatedCode = codegen.generateCode(model);
		return generatedCode;
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

}

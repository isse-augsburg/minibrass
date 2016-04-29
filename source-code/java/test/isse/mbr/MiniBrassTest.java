package isse.mbr;

import java.io.File;
import java.io.FileNotFoundException;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;

public class MiniBrassTest {

	public static void main(String[] args) throws FileNotFoundException, MiniBrassParseException {
		MiniBrassParser parser = new MiniBrassParser(); 
		MiniBrassAST model = parser.parse(new File("crsimple.mbr"));
		CodeGenerator codegen = new CodeGenerator();
		codegen.generateCode(model);
	}

}

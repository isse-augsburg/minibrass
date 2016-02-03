package isse.mbr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import isse.mbr.model.MiniBrassModel;
import isse.mbr.model.types.BoolType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.PVSType;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.model.types.SetType;

/**
 * 
 * Parses a MiniBrass file using an appropriate lexer
 * just a simple recursive descent parser
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassParser {
	private Scanner scanner;
	private MiniBrassSymbol currSy;
	private MiniBrassLexer lexer;
	private Set<File> visited;
	private Set<File> worklist;
	private MiniZincVarType lastType;
	private MiniBrassModel model; 
	
	public MiniBrassModel parse(File file) throws FileNotFoundException {
		model = new MiniBrassModel();
		
		worklist = new HashSet<File>();
		worklist.add(file);
		visited = new HashSet<>();
		
		try{
			while(!worklist.isEmpty()) {
				File next = worklist.iterator().next();
				worklist.remove(next);
				
				scanner = new Scanner(next);
				lexer = new MiniBrassLexer();
				lexer.setScanner(scanner);
				lexer.readNextChar(); // to initialize
				
				// top level non-terminal, initialize before
				getNextSy();
				miniBrassFile();
				
				visited.add(next);
			}
			
		} catch(MiniBrassParseException ex){
			System.err.println(ex.getMessage());
		}
		finally {
			if(scanner != null)
				scanner.close();
		}
		return model;
	}

	/**
	 * statement (statement)+
	 * @throws MiniBrassParseException 
	 */
	private void miniBrassFile() throws MiniBrassParseException {
		while(currSy != MiniBrassSymbol.EofSy) {
			item();
		}
	}

	private void item() throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.IncludeSy) {
			getNextSy();
			includeItem();
		} else if(currSy == MiniBrassSymbol.TypeSy) {
			getNextSy();
			typeItem();
		}
	}

	/**
	 * "type" ident "=" "PVS" "(" MiniZincType"," MZN-Literal"," MZN-Literal"," MZN-Literal ")" 
	 * @throws MiniBrassParseException 
	 */
	private void typeItem() throws MiniBrassParseException {
		System.out.println("In type item");
		expectSymbol(MiniBrassSymbol.IdentSy);
		String reference = lexer.getLastIdent();
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		expectSymbolAndNext(MiniBrassSymbol.PVSSy);
				
		expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);		
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String name = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.CommaSy);
		
		MiniZincVarType elementType = MiniZincVarType();
		expectSymbolAndNext(MiniBrassSymbol.CommaSy);
		
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String combination = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.CommaSy);
		
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String order = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.CommaSy);
		
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String top = lexer.getLastIdent();
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		
		PVSType nextPVSType = new PVSType(elementType, name, combination,order,top);
		model.registerPVSType(reference, nextPVSType);
	}

	private void expectSymbolAndNext(MiniBrassSymbol sy) throws MiniBrassParseException {
		expectSymbol(sy);
		getNextSy();
	}

	/**
	 * set of PRIMTYPE | PRIMTYPE
	 * @throws MiniBrassParseException 
	 */
	private MiniZincVarType MiniZincVarType() throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.SetSy) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.OfSy);
			getNextSy();
			PrimitiveType pt = primType();
			return new SetType(pt);
		} else {
			return primType();
		}
	}

	private PrimitiveType primType() throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.FloatSy || currSy == MiniBrassSymbol.BoolSy || currSy == MiniBrassSymbol.IntSy) {
			switch(currSy) {
			case FloatSy:
				getNextSy();
				return new FloatType();
			case BoolSy:
				getNextSy();
				return new BoolType();
			case IntSy:
				getNextSy();
				return new IntType();
			default:
				break;
			}
		} else if (currSy == MiniBrassSymbol.IntLitSy) {
			int lower = lexer.getLastInt();
			getNextSy();
			expectSymbol(MiniBrassSymbol.DotsSy);
			getNextSy();
			expectSymbol(MiniBrassSymbol.IntLitSy);
			int upper = lexer.getLastInt();
			
			getNextSy();
			return new IntType(lower, upper);
		} else if (currSy == MiniBrassSymbol.FloatLitSy) {
			double lower = lexer.getLastFloat();
			getNextSy();
			expectSymbol(MiniBrassSymbol.DotsSy);
			getNextSy();
			expectSymbol(MiniBrassSymbol.FloatLitSy);
			double upper = lexer.getLastFloat();
			
			getNextSy();
			return new FloatType(lower, upper);
		}
		throw new MiniBrassParseException("Expected a MiniZinc var type for PVS declaration");
	}

	/**
	 * "include" ident ";" (just read ident, when entering
	 * @throws MiniBrassParseException 
	 */
	private void includeItem() throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.StringLitSy);
		// add this file to our work list
		String fileName = lexer.getLastIdent();
		
		getNextSy();
		
		File referred = new File(fileName);
		if(!visited.contains(referred)) {
			worklist.add(referred);
		}
		expectSymbol(MiniBrassSymbol.SemicolonSy);
		getNextSy();
	}

	private void expectSymbol(MiniBrassSymbol expectedSy) throws MiniBrassParseException {
		if(currSy != expectedSy) {
			throw new MiniBrassParseException("Error at line "+lexer.getLineNo()+" ("+lexer.getColPtr()+"): Expected symbol "+expectedSy +  " but found "+currSy);
		}
	}

	private void getNextSy() {
		currSy = lexer.getNextSymbol();
		System.out.println("Returning symbol: "+currSy);
	}


}

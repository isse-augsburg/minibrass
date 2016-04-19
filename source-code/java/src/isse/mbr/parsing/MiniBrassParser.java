package isse.mbr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.types.BoolType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.NamedRef;
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
	private MiniBrassAST model; 
	
	public MiniBrassAST parse(File file) throws FileNotFoundException {
		model = new MiniBrassAST();
		
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
			
			System.err.println("Error at line "+lexer.getLineNo()+" ("+lexer.getColPtr()+"): " + ex.getMessage());
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
			PVSType pvsType = typeItem();
		} else if (currSy == MiniBrassSymbol.MorphismSy) {
			getNextSy();
			morphismItem();
		}
		else {
			throw new MiniBrassParseException("Unexpected symbol when looking for item: "+currSy);
		}
	}

	/**
	 * 	morphism ConstraintRelationships -> WeightedCsp: ToWeighted = weight_cr;
	 */
	private void morphismItem() throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.IdentSy);
		String morphFrom = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.ArrowSy);
		
		expectSymbol(MiniBrassSymbol.IdentSy);
		String morphTo = lexer.getLastIdent();
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.ColonSy);
		expectSymbol(MiniBrassSymbol.IdentSy);
		String morphName = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);

		expectSymbol(MiniBrassSymbol.IdentSy);
		String mznFunctionName = lexer.getLastIdent();
		getNextSy();

		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		System.out.println("Morphism "+morphName+" mapping "+morphFrom + " to "+morphTo + " with mzn function "+mznFunctionName);
	}

	/**
	 * "type" ident "=" "PVSType" "<" MiniZincType ( "," MiniZincType) ">" 
	 * @throws MiniBrassParseException 
	 */
	private PVSType typeItem() throws MiniBrassParseException {
		
		PVSType newType = new PVSType();
		
		System.out.println("In type item");
		expectSymbol(MiniBrassSymbol.IdentSy);
		newType.setName(lexer.getLastIdent());
		System.out.println("Working on type ... "+newType.getName());
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		expectSymbolAndNext(MiniBrassSymbol.PVSTypeSy);
				
		expectSymbolAndNext(MiniBrassSymbol.LeftAngleSy);		
		
		MiniZincVarType specType = MiniZincVarType();
		MiniZincVarType elementType = specType;
		
		System.out.println("Specification type: "+elementType);
		if(currSy == MiniBrassSymbol.CommaSy) {
			// Read the element type as well
			getNextSy();
			elementType = MiniZincVarType();
		}
		
		newType.setElementType(elementType);
		newType.setSpecType(specType);
		
		expectSymbolAndNext(MiniBrassSymbol.RightAngleSy);		
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		if(currSy == MiniBrassSymbol.ParamsSy) { // we have a parameters part
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			
			while(currSy != MiniBrassSymbol.RightCurlSy) {
				parameterDecl();
			}
			expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
			System.out.println("Standing here, which symbol? " + currSy + " ; " + lexer.getLastIdent());
			expectSymbolAndNext(MiniBrassSymbol.InSy);
		} 
		
		expectSymbolAndNext(MiniBrassSymbol.InstantiatesSy);
		expectSymbolAndNext(MiniBrassSymbol.WithSy);
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String fileName = lexer.getLastIdent();
		System.out.println("Look for in file : "+ fileName);
		
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
		while(currSy != MiniBrassSymbol.RightCurlSy) {
			mapping();
		}
		getNextSy();
		if(currSy == MiniBrassSymbol.SemicolonSy) 
			getNextSy(); // this should be EOF if only comments follow ...
		
		return newType;
	}

	/**
	 * Things like
	 *    int: k; 
   	 *    array[1..nScs] of 1..k: weights;
   	 *    array[int, 1..2] of 1..nScs: crEdges;
	 * @throws MiniBrassParseException 
	 */
	private void parameterDecl() throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.ArraySy) {
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);
			MiniZincVarType indexType = MiniZincVarType();
			if (!(indexType instanceof IntType)) {
				throw new MiniBrassParseException("Index type has to be an integer range! " + indexType);
			}
			
			System.out.println("Main index: "+indexType);
			while(currSy != MiniBrassSymbol.RightBracketSy) { 
				expectSymbolAndNext(MiniBrassSymbol.CommaSy);
				
				indexType = MiniZincVarType();
				System.out.println("  Next index: "+indexType);
				if (!(indexType instanceof IntType)) {
					throw new MiniBrassParseException("Index type has to be an integer range! " + indexType);
				}
				
			}
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.OfSy);
			MiniZincVarType varType = MiniZincVarType();
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		} else {
			MiniZincVarType varType = MiniZincVarType(); // could be int
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String ident = lexer.getLastIdent();
			
			System.out.println("Registering parameter "+varType + ": "+ident);
		
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		}
	}

	/**
	 * Is either of times -> xyz, is_worse ..., top ... 
	 * PVS-Sym "->" AnyCharacters 
	 * @throws MiniBrassParseException 
	 */
	private void mapping() throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.IdentSy);
		// must be either times, is_worse, top or bot
		List<String> validIdents = Arrays.asList("times", "is_worse", "top", "bot");
		String readIdent = lexer.getLastIdent();
		if(validIdents.contains(readIdent)) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.ArrowSy);
			if(readIdent.equals("top") || readIdent.equals("bot")) {
				String verbatimExpr = readVerbatimUntil(';');
				System.out.println("Read verbatim: "+verbatimExpr);
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			} else {
				getNextSy();
				expectSymbol(MiniBrassSymbol.IdentSy);
				String function = lexer.getLastIdent();
				System.out.println("Read function: "+function);
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			}
			// map.put(readIdent, targetExpression)
		} else {
			throw new MiniBrassParseException("Expecting identifier in "+Arrays.toString(validIdents.toArray()) + " instead of "+readIdent);
		}
	}

	private String readVerbatimUntil(char c) {
		return lexer.readVerbatimUntil(c);
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
				throw new MiniBrassParseException("This should not happen");
			}
		} else {
			return intervalType();
		}
		
	}

	/**
	 * For now : intLit | floatLit | ident ".." intLit | floatLit | ident
	 * @return
	 * @throws MiniBrassParseException
	 */
	private PrimitiveType intervalType() throws MiniBrassParseException {
		Object lower = getNumericExpr();
		expectSymbolAndNext(MiniBrassSymbol.DotsSy);
		Object upper = getNumericExpr();
		
		if(lower instanceof Double || upper instanceof Double) {
			// treat as floatType
			NamedRef<Double> lRef = new NamedRef<Double>(lower);
			NamedRef<Double> uRef = new NamedRef<Double>(upper);
			return new FloatType(lRef, uRef);
		} else {
			// treat as int
			NamedRef<Integer> lRef = new NamedRef<Integer>(lower);
			NamedRef<Integer> uRef = new NamedRef<Integer>(upper);
			return new IntType(lRef, uRef);
		}
	}

	private Object getNumericExpr() throws MiniBrassParseException {
		if (currSy == MiniBrassSymbol.IntLitSy) {
			Integer value = lexer.getLastInt();
			getNextSy();
			return value;
		} else if (currSy == MiniBrassSymbol.FloatLitSy) {
			Double value = lexer.getLastFloat();
			getNextSy();
			return value;
		} else if (currSy == MiniBrassSymbol.IdentSy) {
			String value = lexer.getLastIdent();
			getNextSy();
			return value;
		}
		throw new MiniBrassParseException("Expected IntLitSy or FloatLitSy or Ident");
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
			throw new MiniBrassParseException("Expected symbol "+expectedSy +  " but found "+currSy);
		}
	}

	private void getNextSy() {
		currSy = lexer.getNextSymbol();
		//System.out.println("Returning symbol: "+currSy);
	}


}

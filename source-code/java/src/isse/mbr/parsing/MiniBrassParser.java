package isse.mbr.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.parsetree.ProductType;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.BoolType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PVSParameter;
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
	private MiniBrassAST model; 
	private SemanticChecker semChecker;
	
	public MiniBrassAST parse(File file) throws FileNotFoundException, MiniBrassParseException {
		model = new MiniBrassAST();
		semChecker = new SemanticChecker();
		
		worklist = new HashSet<File>();
		worklist.add(file);
		visited = new HashSet<>();
		
		try{
			while(!worklist.isEmpty()) {
				File next = worklist.iterator().next();
				worklist.remove(next);
				
				if(!visited.contains(next)) {
					scanner = new Scanner(next);
					lexer = new MiniBrassLexer();
					lexer.setScanner(scanner);
					lexer.readNextChar(); // to initialize
					
					// top level non-terminal, initialize before
					getNextSy();
					miniBrassFile(model);
				}
				visited.add(next);
				
			}
			
			// now do a first consistency check of pending references 
			semChecker.updateReferences();
			semChecker.executeArrayJobs();
			semChecker.checkPvsInstances(model);
			
			System.out.println("I should optimize: "+model.getSolveInstance().instance);
			for(Entry<String, AbstractPVSInstance> entry: model.getPvsInstances().entrySet()){
				System.out.println("Got instance: " + entry.getValue().toString());
			}
		} catch(MiniBrassParseException ex){
			
			System.err.println("Error at line "+lexer.getLineNo()+" ("+lexer.getColPtr()+"): " + ex.getMessage());
			throw ex;
		}
		finally {
			if(scanner != null)
				scanner.close();
		}
		return model;
	}

	/**
	 * statement (statement)+
	 * @param model2 
	 * @throws MiniBrassParseException 
	 */
	private void miniBrassFile(MiniBrassAST model) throws MiniBrassParseException {
		
		while(currSy != MiniBrassSymbol.EofSy) {
			item(model);
		}
	}

	private void item(MiniBrassAST model) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.IncludeSy) {
			getNextSy();
			includeItem();
		} else if(currSy == MiniBrassSymbol.TypeSy) {
			getNextSy();
			PVSType pvsType = typeItem();
			model.registerPVSType(pvsType.getName(), pvsType);
		} else if (currSy == MiniBrassSymbol.MorphismSy) {
			getNextSy();
			morphismItem();
		} else if (currSy == MiniBrassSymbol.SolveSy) {
			if(model.getSolveInstance() != null) {
				throw new MiniBrassParseException("More than one solve item specified !");
			}
			getNextSy();
			String solveInstance = solveItem();
			NamedRef<AbstractPVSInstance> namedRef = new NamedRef<AbstractPVSInstance>(solveInstance);
			semChecker.scheduleUpdate(namedRef, model.getPvsReferences());
			
			model.setSolveInstance(namedRef ); 
		} else if(currSy == MiniBrassSymbol.PvsSy) { 
			getNextSy();
			pvsInstItem(model);
		}
		else {
			throw new MiniBrassParseException("Unexpected symbol when looking for item: "+currSy + " (last ident -> " +lexer.getLastIdent() +")");
		}
	}

	/**
	 * PVS: fuzzyInstance = new FuzzyCsp(3); 
	 * PVS: weightedInstance = new WeightedCsp(3, 8, [2,1,2]);
	 * PVS: cr = new ConstraintRelationships(2, [| 1, 2 |]);
	 * PVS: hierarchy = cr lex fuzzyInstance
	 *    
	 * PVSItem   -> "PVS" ":" ident "=" PVSInst ";"
	 * PVSInst   ->  PVSDiProd ( "lex" PVSDiProd )*
	 * PVSDiProd ->  PVSAtom ("*" PVSAtom)*
	 * PVSAtom   -> "new" ident "(" stringlit "," int ("," MZNLiteral)* ")" ";"         
	 *            |  ident
	 *            
	 * pvs1, pvs2, pvs3
	 * pvs1 lex pvs2 * pvs3 (should be read as pvs1 lex (pvs2 * pvs3) 
	 * pvs1 * pvs2 lex pvs3 
	 * @param model2 
	 * @throws MiniBrassParseException
	 */
	private void pvsInstItem(MiniBrassAST model) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.ColonSy) { // tolerated, but not required
			getNextSy();
		}
		expectSymbol(MiniBrassSymbol.IdentSy);
		String newPvsRef = lexer.getLastIdent();
		getNextSy();

		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		
		AbstractPVSInstance pvsInstance = PVSInst(model);
		
		System.out.println("Got instance: ");
		System.out.println(pvsInstance);
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		
		if(model.getPvsReferences().containsKey(newPvsRef)) {
			throw new MiniBrassParseException("PVS reference "+newPvsRef + " already defined ("+model.getPvsReferences().get(newPvsRef));
		} else {
			model.getPvsReferences().put(newPvsRef, pvsInstance);
		}
 	}	

	/**
	 * PVSInst   ->  PVSDiProd ( "lex" PVSDiProd )
	 * @param model2 *
	 * @return
	 * @throws MiniBrassParseException
	 */
	private AbstractPVSInstance PVSInst(MiniBrassAST model) throws MiniBrassParseException {

		AbstractPVSInstance first =  PVSDiProd(model);
		
		while(currSy == MiniBrassSymbol.LexSy){
			getNextSy();
			
			AbstractPVSInstance next = PVSDiProd(model);
			
			CompositePVSInstance composite = new CompositePVSInstance();
			composite.setLeftHandSide(first);
			composite.setProductType(ProductType.LEXICOGRAPHIC);
			composite.setRightHandSide(next);
			
			first = composite;
		}
		return first;
	}

	/**
	 * PVSDiProd ->  PVSAtom ("*" PVSAtom)
	 * @return *
	 */
	private AbstractPVSInstance PVSDiProd(MiniBrassAST model) throws MiniBrassParseException {
	
		AbstractPVSInstance first = PVSAtom(model);
		while(currSy == MiniBrassSymbol.AsteriskSy) {
			getNextSy();
			
			AbstractPVSInstance next = PVSAtom(model);
			
			CompositePVSInstance composite = new CompositePVSInstance();
			composite.setLeftHandSide(first);
			composite.setProductType(ProductType.DIRECT);
			composite.setRightHandSide(next);
			
			first = composite;
		}
		return first;
	}

	/**
	 * PVSAtom   -> "new" ident "(" stringlit "," intlit ("," MZNLiteral)* ")"          
	 *            |  ident
	 *            | "(" PVSInst ")"
	 * @throws MiniBrassParseException 
	 * 
	 */
	private AbstractPVSInstance PVSAtom(MiniBrassAST model) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.NewSy) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.IdentSy);
			String typeId = lexer.getLastIdent();
			NamedRef<PVSType> typeRef = new NamedRef<>(typeId);
			semChecker.scheduleUpdate(typeRef, model.getPvsTypes());
			
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
			expectSymbol(MiniBrassSymbol.StringLitSy);
			String name = lexer.getLastIdent();
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.CommaSy);
			expectSymbol(MiniBrassSymbol.IntLitSy);
			int nScs = lexer.getLastInt();
			
			PVSInstance instance = new PVSInstance();
			
			instance.setName(name);
			instance.setType(typeRef);
			instance.setNumberSoftConstraints(nScs);
			
			getNextSy();
			while(currSy == MiniBrassSymbol.CommaSy) {
				getNextSy();
				String mznLiteral = MznLiteral(model);
				instance.getParameterValues().add(mznLiteral.trim());
			}
			expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
			
			// register the newly created instance
			model.getPvsInstances().put(instance.getName(), instance);
			return instance;
		} else if (currSy == MiniBrassSymbol.IdentSy) {
			String reference = lexer.getLastIdent();
			ReferencedPVSInstance referencedPVSInstance = new ReferencedPVSInstance();
			referencedPVSInstance.setReference(reference);
			referencedPVSInstance.setName("RefTo-"+reference);
			
			getNextSy();
			return referencedPVSInstance;
		} else if(currSy == MiniBrassSymbol.LeftParenSy) {
			getNextSy();
			AbstractPVSInstance inst = PVSInst(model);
			expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
			
			return inst;
		} else {
			throw new MiniBrassParseException("Expected 'new' or identifier as atomic PVS");
		}
	}

	/**
	 * "[" digit ("," digit)+ "]" | "[|" digit ("," (digit|"|") )+ "|]" | ident | "{}"
	 * @param model
	 * @return
	 * @throws MiniBrassParseException 
	 */
	private String MznLiteral(MiniBrassAST model) throws MiniBrassParseException {
		// TODO Auto-generated method stub
		if(currSy == MiniBrassSymbol.LeftBracketSy) { // [ 1, 2, 4] [| 12 | 12 | |]
			String remainder = lexer.readVerbatimUntil(']');
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.RightBracketSy);
			return '['+remainder+']';
		} else if (currSy == MiniBrassSymbol.LeftCurlSy) {
			String remainder = lexer.readVerbatimUntil('}');
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
			return '{'+remainder+'}';
		} else if (currSy == MiniBrassSymbol.IntLitSy) {
			String val = Integer.toString(lexer.lastInt);
			getNextSy();
			return val;
		} else if (currSy == MiniBrassSymbol.FloatLitSy) {
			String val = Double.toString(lexer.lastFloat);
			getNextSy();
			return val;
		} else if (currSy == MiniBrassSymbol.IdentSy) {
			String id = lexer.getLastIdent();
			getNextSy();
			return id;
		}
		throw new MiniBrassParseException("Unexpected symbol: "+currSy + " in MiniZinc literal.");
	}

	/**
	 * solve ident ;
	 * @throws MiniBrassParseException 
	 */
	private String solveItem() throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.IdentSy);
		String identifier = lexer.getLastIdent();
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		return identifier;
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
		
		if(model.getPvsTypes().containsKey(newType.getName())) {
			throw new MiniBrassParseException("Type "+newType.getName() + " already defined!");
		}
		
		System.out.println("Working on type ... "+newType.getName());
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		expectSymbolAndNext(MiniBrassSymbol.PVSTypeSy);
				
		expectSymbolAndNext(MiniBrassSymbol.LeftAngleSy);		
		
		MiniZincVarType specType = MiniZincVarType(newType);
		MiniZincVarType elementType = specType;
		
		System.out.println("Specification type: "+elementType);
		if(currSy == MiniBrassSymbol.CommaSy) {
			// Read the element type as well
			getNextSy();
			elementType = MiniZincVarType(newType);
		}
		
		newType.setElementType(elementType);
		newType.setSpecType(specType);
		
		expectSymbolAndNext(MiniBrassSymbol.RightAngleSy);		
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		if(currSy == MiniBrassSymbol.ParamsSy) { // we have a parameters part
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			
			while(currSy != MiniBrassSymbol.RightCurlSy) {
				PVSParameter par = parameterDecl(newType);
				
				newType.addPvsParameter(par);
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
		newType.setImplementationFile(fileName);
		
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
		while(currSy != MiniBrassSymbol.RightCurlSy) {
			mapping(newType);
		}
		getNextSy();
		if(currSy == MiniBrassSymbol.SemicolonSy) 
			getNextSy(); // this should be EOF if only comments follow ...
		
		return newType;
	}

	/**
	 * Things like
	 *    int: k; 
	 *    1..k: top;
   	 *    array[1..nScs] of 1..k: weights;
   	 *    array[int, 1..2] of 1..nScs: crEdges;
	 * @param newType 
	 * @return 
	 * @throws MiniBrassParseException 
	 */
	private PVSParameter parameterDecl(PVSType scopeType) throws MiniBrassParseException {
		PVSParameter returnParameter;
		if(currSy == MiniBrassSymbol.ArraySy) {
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);
			
			ArrayType arrayType = new ArrayType();
					
			MiniZincVarType indexType = MiniZincVarType(scopeType);
			
			List<MiniZincVarType> pendingIndexTypes = new LinkedList<>();
			pendingIndexTypes.add(indexType);
			
			System.out.println("Main index: "+indexType);
			while(currSy != MiniBrassSymbol.RightBracketSy) { 
				expectSymbolAndNext(MiniBrassSymbol.CommaSy);
				
				indexType = MiniZincVarType(scopeType);
				System.out.println("  Next index: "+indexType);
				pendingIndexTypes.add(indexType);				
			}
			
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.OfSy);
			MiniZincVarType varType = MiniZincVarType(scopeType);
			
			arrayType.setType(varType);
			
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String name = lexer.getLastIdent();
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);

			semChecker.scheduleArrayTypeCheck(arrayType, pendingIndexTypes, name);
			
			// dependency checks 
			semChecker.scheduleTypeDependencyCheck(scopeType, name, varType);
			for(MiniZincVarType pendingVarType : pendingIndexTypes) {
				semChecker.scheduleTypeDependencyCheck(scopeType, name, pendingVarType);
			}
			
			returnParameter = new PVSParameter(name, arrayType);
		} else {
			MiniZincVarType varType = MiniZincVarType(scopeType); // could be int
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String ident = lexer.getLastIdent();
			
			System.out.println("Registering parameter "+varType + ": "+ident);
		
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			semChecker.scheduleTypeDependencyCheck(scopeType, ident, varType);
			returnParameter = new PVSParameter(ident, varType);
		}
		return returnParameter;
	}

	/**
	 * Is either of times -> xyz, is_worse ..., top ... 
	 * PVS-Sym "->" AnyCharacters 
	 * @param newType 
	 * @throws MiniBrassParseException 
	 */
	private void mapping(PVSType newType) throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.IdentSy);
		// must be either times, is_worse, top or bot
		List<String> validIdents = Arrays.asList("times", "is_worse", "top", "bot");
		String readIdent = lexer.getLastIdent();
		String targetFunction = null;
		
		if(validIdents.contains(readIdent)) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.ArrowSy);
			if(readIdent.equals("top") || readIdent.equals("bot")) {
				targetFunction = readVerbatimUntil(';');
				System.out.println("Read verbatim: "+targetFunction);
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			} else {
				getNextSy();
				expectSymbol(MiniBrassSymbol.IdentSy);
				targetFunction = lexer.getLastIdent();
				System.out.println("Read function: "+targetFunction);
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			}
			switch(readIdent) {
			case "times":
				newType.setCombination(targetFunction);
				break;
			case "is_worse":
				newType.setOrder(targetFunction);
				break;
			case "top":
				newType.setTop(targetFunction);
				break;
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
	private MiniZincVarType MiniZincVarType(PVSType scopeType) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.SetSy) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.OfSy);
			getNextSy();
			PrimitiveType pt = primType(scopeType);
			return new SetType(pt);
		} else {
			return primType(scopeType);
		}
	}

	private PrimitiveType primType(PVSType scopeType) throws MiniBrassParseException {
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
			return intervalType(scopeType);
		}
		
	}

	/**
	 * For now : intLit | floatLit | ident ".." intLit | floatLit | ident
	 * @return
	 * @throws MiniBrassParseException
	 */
	private PrimitiveType intervalType(PVSType scopeType) throws MiniBrassParseException {
		NumericValue lower = getNumericExpr(scopeType);
		expectSymbolAndNext(MiniBrassSymbol.DotsSy);
		NumericValue upper = getNumericExpr(scopeType);
		
		return new IntervalType(lower, upper);
	}

	private NumericValue getNumericExpr(PVSType scopeType) throws MiniBrassParseException {
		if (currSy == MiniBrassSymbol.IntLitSy) {
			Integer value = lexer.getLastInt();
			getNextSy();
			return new NumericValue(value);
		} else if (currSy == MiniBrassSymbol.FloatLitSy) {
			Double value = lexer.getLastFloat();
			getNextSy();
			return new NumericValue(value);
		} else if (currSy == MiniBrassSymbol.IdentSy) {
			String value = lexer.getLastIdent();
			getNextSy();
			NumericValue ref = new NumericValue(value);
			semChecker.scheduleParameterReference(ref.getReferencedParameter(), scopeType);
			return ref;
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

package isse.mbr.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import isse.mbr.extensions.ExternalMorphism;
import isse.mbr.extensions.ExternalParameterWrap;
import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.MorphedPVSInstance;
import isse.mbr.model.parsetree.Morphism;
import isse.mbr.model.parsetree.Morphism.ParamMapping;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.parsetree.ProductType;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.parsetree.SoftConstraint;
import isse.mbr.model.parsetree.VotingInstance;
import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.BoolType;
import isse.mbr.model.types.FloatType;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincParType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.MultiSetType;
import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PVSFormalParameter;
import isse.mbr.model.types.PVSType;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.model.types.SetType;
import isse.mbr.model.voting.VotingFactory;
import isse.mbr.model.voting.VotingProcedure;

/**
 * 
 * Parses a MiniBrass file using an appropriate lexer
 * just a simple recursive descent parser
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassParser {


	private final static Logger LOGGER = Logger.getGlobal();
	
	private Scanner scanner;
	private MiniBrassSymbol currSy;
	private MiniBrassLexer lexer;
	private Set<File> visited;
	private Set<InputStream> visitedStreams;
	private Set<File> worklist;
	private Set<InputStream> worklistOfStreams;
	private File currDir;
	private File mbrStdDir;
	private MiniBrassAST model; 
	private SemanticChecker semChecker;
	private int productCounter;
	
	public MiniBrassParser() {
	}
	
	public MiniBrassAST parse(InputStream input) throws MiniBrassParseException {
		
		model = new MiniBrassAST();
		semChecker = new SemanticChecker();
		
		worklistOfStreams = new HashSet<>();
		worklistOfStreams.add(input);
		
		visited = new HashSet<>();
		visitedStreams = new HashSet<>();
		
		productCounter = 0;
		mbrStdDir = null;
		
		try{
			URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();	

			try {
				File classPathDir = new File(jarLocation.toURI());
				File siblingStd = new File(classPathDir.getParentFile(), "mbr_std");
				if(siblingStd.exists())
					mbrStdDir = siblingStd;
				else {
					File cousinStd = new File(classPathDir.getParentFile().getParentFile(), "mbr_std");
					if(cousinStd.exists())
						mbrStdDir = cousinStd;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			while(!worklistOfStreams.isEmpty()) {
				InputStream next = worklistOfStreams.iterator().next();
				worklistOfStreams.remove(next);
			
				
				if(!visitedStreams.contains(next)) {
					scanner = new Scanner(next);
					lexer = new MiniBrassLexer();
					lexer.setScanner(scanner);
					lexer.readNextChar(); // to initialize
					
					// top level non-terminal, initialize before
					getNextSy();
					miniBrassFile(model);
				}
				visitedStreams.add(next);
				
			}
			
			// now do a first consistency check of pending references 
			semChecker.updateReferences();
			semChecker.executeArrayJobs();
			semChecker.checkPvsInstances(model);
			semChecker.checkMorphisms();
			
			LOGGER.finer("I should optimize: "+model.getSolveInstance());
			for(Entry<String, AbstractPVSInstance> entry: model.getPvsInstances().entrySet()){
				LOGGER.fine("Got instance: " + entry.getValue().toString());
			}
		} catch(MiniBrassParseException ex){
			
			LOGGER.severe("Error at line "+lexer.getLineNo()+" ("+lexer.getColPtr()+"): " + ex.getMessage());
			throw ex;
		}
		finally {
			if(scanner != null)
				scanner.close();
		}
		return model;
	}

	public MiniBrassAST parse(File file) throws FileNotFoundException, MiniBrassParseException {
		
		model = new MiniBrassAST();
		semChecker = new SemanticChecker();
		
		worklist = new HashSet<>();
		worklist.add(file);
		visited = new HashSet<>();
		
		productCounter = 0;
		mbrStdDir = null;
		
		try{
			URL jarLocation = getClass().getProtectionDomain().getCodeSource().getLocation();	

			try {
				File classPathDir = new File(jarLocation.toURI());
				File siblingStd = new File(classPathDir.getParentFile(), "mbr_std");
				if(siblingStd.exists())
					mbrStdDir = siblingStd;
				else {
					File cousinStd = new File(classPathDir.getParentFile().getParentFile(), "mbr_std");
					if(cousinStd.exists())
						mbrStdDir = cousinStd;
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			while(!worklist.isEmpty()) {
				File next = worklist.iterator().next();
				worklist.remove(next);
				currDir = next.getParentFile();
				
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
			semChecker.checkMorphisms();
			
			LOGGER.finer("I should optimize: "+model.getSolveInstance());
			for(Entry<String, AbstractPVSInstance> entry: model.getPvsInstances().entrySet()){
				LOGGER.fine("Got instance: " + entry.getValue().toString());
			}
		} catch(MiniBrassParseException ex){
			
			LOGGER.severe("Error at line "+lexer.getLineNo()+" ("+lexer.getColPtr()+"): " + ex.getMessage());
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

	/**
	 * The main method for all kinds of items 
	 * @param model
	 * @throws MiniBrassParseException
	 */
	private void item(MiniBrassAST model) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.IncludeSy) {
			getNextSy();
			includeItem(model);
		} else if(currSy == MiniBrassSymbol.TypeSy) {
			getNextSy();
			PVSType pvsType = typeItem();
			model.registerPVSType(pvsType.getName(), pvsType);
		} else if (currSy == MiniBrassSymbol.MorphismSy) {
			getNextSy();
			Morphism m = morphismItem();
			model.registerMorphism(m.getName(), m);
			semChecker.validateMorphism(m);
		} else if (currSy == MiniBrassSymbol.SolveSy) {
			if(model.getSolveInstance() != null) {
				throw new MiniBrassParseException("More than one solve item specified !");
			}
			getNextSy();
			AbstractPVSInstance solveInstance = solveItem();
			
			model.setSolveInstance(solveInstance); 
		} else if(currSy == MiniBrassSymbol.PvsSy) { 
			getNextSy();
			pvsInstItem(model);
		} else if(currSy == MiniBrassSymbol.OutputSy) {
			getNextSy();
			outputItem(model);
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
		
		// in case we have a composite pvs, we can use the reference identifier as name, otherwise just use anonymous name 
		if(pvsInstance instanceof CompositePVSInstance) {
			pvsInstance.setName(newPvsRef);
		}
		
		LOGGER.fine("Got instance: ");
		LOGGER.fine(pvsInstance.toString());
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		
		if(model.getPvsReferences().containsKey(newPvsRef)) {
			throw new MiniBrassParseException("PVS reference "+newPvsRef + " already defined ("+model.getPvsReferences().get(newPvsRef));
		} else {
			model.getPvsReferences().put(newPvsRef, pvsInstance);
		}
 	}	

	/**
	 * OutputItem -> "output" StringLit ";"
	 * @param model
	 * @throws MiniBrassParseException 
	 */
	private void outputItem(MiniBrassAST model) throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.StringLitSy);
		String problemOutput = lexer.getLastIdent();
		model.setProblemOutput(problemOutput);
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
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
			composite.setName("MBR_LEX_"+(++productCounter));
						
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
		while(currSy == MiniBrassSymbol.ParetoSy) {
			getNextSy();
			
			AbstractPVSInstance next = PVSAtom(model);
			
			CompositePVSInstance composite = new CompositePVSInstance();
			composite.setLeftHandSide(first);
			composite.setProductType(ProductType.DIRECT);
			composite.setRightHandSide(next);
			composite.setName("MBR_DIR_"+(++productCounter));
			
			first = composite;
		}
		return first;
	}

	/**
	 * PVSAtom   -> "new" ident "(" stringlit "," intlit ("," MZNLiteral)* ")"           
	 *            |  ident
	 *            |  ident "(" PVSAtom ")"
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
			semChecker.checkPvsInstanceName(name);
			getNextSy();

			expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			
			// register the newly created instance
			PVSInstance instance = new PVSInstance();
						 
			int nScs = 0;
			while(currSy != MiniBrassSymbol.RightCurlSy) {
				// expect items (either soft constraint item or parameter value)
				// surely has to be an ident; Idents do not have hyphens so unfortunately my parser has to deal with that
				expectSymbol(MiniBrassSymbol.IdentSy);
				String ident = lexer.getLastIdent();
				if("soft-constraint".equalsIgnoreCase(ident)) {
					++nScs;
					getNextSy();
					expectSymbol(MiniBrassSymbol.IdentSy);
					String constraintId = lexer.getLastIdent();
					if(instance.getSoftConstraints().containsKey(constraintId))
						throw new MiniBrassParseException("Double definition of constraint '"+constraintId+"' (id "+nScs+").");

					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.ColonSy);
					expectSymbol(MiniBrassSymbol.StringLitSy);
					String mznExpression = lexer.getLastIdent();
					
					if(lexer.getLastStringLitChar() != '\'') {
						throw new MiniBrassParseException("MiniZinc literal expression in instantiations must be enclosed in single quotes.");
					}
					getNextSy();
					SoftConstraint sc = new SoftConstraint(nScs, constraintId, mznExpression);
					
					// here we need optional annotations 
					if(currSy == MiniBrassSymbol.DoubleColonSy) {
						getNextSy();
						expectSymbol(MiniBrassSymbol.IdentSy); 
						String parameterName = lexer.getLastIdent();
						getNextSy();
						expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
						expectSymbol(MiniBrassSymbol.StringLitSy);
						if(lexer.getLastStringLitChar() != '\'') {
							throw new MiniBrassParseException("MiniZinc literal expression in instantiations must be enclosed in single quotes.");
						}
						String content = lexer.getLastIdent();
						// store in annotations linked to soft constraint
						sc.getAnnotations().put(parameterName, content);
						
						getNextSy();
						expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
						
					} 					
					expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
					
					instance.getSoftConstraints().put(constraintId, sc);
				} else {
					String paramName = lexer.getLastIdent();
					getNextSy();	
					expectSymbolAndNext(MiniBrassSymbol.ColonSy);
					expectSymbol(MiniBrassSymbol.StringLitSy);
					String mznExpression = lexer.getLastIdent();
					
					if(lexer.getLastStringLitChar() != '\'') {
						throw new MiniBrassParseException("MiniZinc literal expression in instantiations must be enclosed in single quotes.");
					}
					
					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
					instance.getActualParameterValues().put(paramName, mznExpression);					
				}				
			}
			expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
						
			instance.setName(name);
			instance.setType(typeRef);
			instance.setNumberSoftConstraints(nScs);
			
			instance.getActualParameterValues().put(PVSType.N_SCS_LIT, Integer.toString(nScs));
			model.getPvsInstances().put(instance.getName(), instance);
			return instance;
		} else if (currSy == MiniBrassSymbol.IdentSy) {
			String reference = lexer.getLastIdent();

			getNextSy();
			if(currSy == MiniBrassSymbol.LeftParenSy) {
				getNextSy();
				AbstractPVSInstance pvs = PVSAtom(model);
				expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
				MorphedPVSInstance morphedInst = new MorphedPVSInstance();
				morphedInst.setInput(pvs);
				morphedInst.setMorphism(new NamedRef<Morphism>(reference));
				semChecker.scheduleUpdate(morphedInst.getMorphism(), model.getMorphisms());
				morphedInst.setName(reference+"_"+pvs.getName()+"_");
				model.getPvsInstances().put(morphedInst.getName(), morphedInst);
		
				return morphedInst;
			} else {
				ReferencedPVSInstance referencedPVSInstance = new ReferencedPVSInstance();
				referencedPVSInstance.setReference(reference);
				referencedPVSInstance.setName("RefTo_"+reference);
				referencedPVSInstance.setReferencedInstance(new NamedRef<AbstractPVSInstance>(reference));
				semChecker.scheduleUpdate(referencedPVSInstance.getReferencedInstance(), model.getPvsReferences());
				
				return referencedPVSInstance;
			}
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
	 * solve ( ident | VoteItem) ;
	 * @throws MiniBrassParseException 
	 */
	private AbstractPVSInstance solveItem() throws MiniBrassParseException {
		AbstractPVSInstance instToSolve = null;
		if(currSy == MiniBrassSymbol.VotingSy) {
			instToSolve = votingInst(model);
		} else {
			instToSolve = PVSInst(model);	
		}
		
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		model.getPvsInstances().put(instToSolve.getName(), instToSolve);
		return instToSolve;
	}

	/**
	 * vote([ident1,...,identn], voteType)
	 * @param model
	 * @return
	 * @throws MiniBrassParseException 
	 */
	private AbstractPVSInstance votingInst(MiniBrassAST model) throws MiniBrassParseException {
		expectSymbolAndNext(MiniBrassSymbol.VotingSy);
		expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
		expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);
		
		VotingInstance votingInst = new VotingInstance(); 
		votingInst.setName("MBR_VOT_"+(++productCounter));
		AbstractPVSInstance nextInst = PVSInst(model);
		votingInst.addPvs(nextInst);
		
		while(currSy == MiniBrassSymbol.CommaSy) {
			getNextSy();
			nextInst = PVSInst(model);
			votingInst.addPvs(nextInst);
		}
			
		expectSymbolAndNext(MiniBrassSymbol.RightBracketSy);
		expectSymbolAndNext(MiniBrassSymbol.CommaSy);
		
		expectSymbol(MiniBrassSymbol.IdentSy);
		String votingType = lexer.getLastIdent();
		VotingProcedure vp = VotingFactory.getVotingProcedure(votingType);
		votingInst.setVotingProcedure(vp);
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
		return votingInst;
	}

	/**
	 * 	morphism ConstraintRelationships -> WeightedCsp: ToWeighted = weight_cr;
	 * @return 
	 */
	private Morphism morphismItem() throws MiniBrassParseException {
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

		Morphism m = new Morphism();
		m.setFrom(new NamedRef<PVSType>(morphFrom));
		m.setTo(new NamedRef<PVSType>(morphTo));
		m.setName(morphName);
		
		// can have params 
		if(currSy == MiniBrassSymbol.ParamsSy) {
			getNextSy();
			if(currSy == MiniBrassSymbol.GeneratedBySy) {
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
				expectSymbol(MiniBrassSymbol.StringLitSy);
				String generationExpression = lexer.getLastIdent();
				try {
					@SuppressWarnings("unchecked")
					Class<? extends ExternalMorphism> externalMorphism = (Class<? extends ExternalMorphism>) Class.forName(generationExpression);
					ExternalMorphism em = externalMorphism.newInstance();
					m.setExternalMorphism(em);
				} catch (ClassNotFoundException e) {
					throw new MiniBrassParseException("Class "+generationExpression + " in morphism "+morphName +" was not found!");
				} catch (InstantiationException e) {
					throw new MiniBrassParseException(e);
				} catch (IllegalAccessException e) {
					throw new MiniBrassParseException(e);
				}
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
			}
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			// now a param item until we see RightCurlSy
			while(currSy != MiniBrassSymbol.RightCurlSy) {
				expectSymbol(MiniBrassSymbol.IdentSy);
				String targetParamName = lexer.getLastIdent();
				ParamMapping pm = new ParamMapping();
				pm.setParam(targetParamName);
				
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
				
				// now either we have a stringlit sy for a minizinc literal expression
				if(currSy == MiniBrassSymbol.StringLitSy) {
					pm.setMznExpression(lexer.getLastIdent());
					getNextSy();
				} else if(currSy == MiniBrassSymbol.GeneratedSy) {
					getNextSy();
					pm.setGenerated(true); // call for external morphism object here
				} else {
					// or we have an ident for a function 
					expectSymbol(MiniBrassSymbol.IdentSy);
					pm.setMznFunction(lexer.getLastIdent());
					getNextSy();
				}
				
				m.getParamMappings().put(pm.getParam(), pm);
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			}
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.InSy);
		}
		expectSymbol(MiniBrassSymbol.IdentSy);
		String mznFunctionName = lexer.getLastIdent();
		getNextSy();

		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		LOGGER.fine("Morphism "+morphName+" mapping "+morphFrom + " to "+morphTo + " with mzn function "+mznFunctionName);
		m.setMznFunction(mznFunctionName);
		
		semChecker.scheduleUpdate(m.getFrom(), model.getPvsTypes());
		semChecker.scheduleUpdate(m.getTo(), model.getPvsTypes());

		return m;
	}

	/**
	 * "type" ident "=" "PVSType" "<" MiniZincType ( "," MiniZincType) ">" [ "represents" ident] 
	 * @throws MiniBrassParseException 
	 */
	private PVSType typeItem() throws MiniBrassParseException {
		
		PVSType newType = new PVSType();
		
		LOGGER.fine("In type item");
		expectSymbol(MiniBrassSymbol.IdentSy);
		newType.setName(lexer.getLastIdent());
		
		if(model.getPvsTypes().containsKey(newType.getName())) {
			throw new MiniBrassParseException("Type "+newType.getName() + " already defined!");
		}
		
		LOGGER.fine("Working on type ... "+newType.getName());
		getNextSy();
		
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		expectSymbolAndNext(MiniBrassSymbol.PVSTypeSy);
				
		expectSymbolAndNext(MiniBrassSymbol.LeftAngleSy);		
		
		// TODO support arrays here
		MiniZincParType specType = MiniZincParType(newType);
		MiniZincParType elementType = specType;
		
		LOGGER.fine("Specification type: "+elementType);
		if(currSy == MiniBrassSymbol.CommaSy) {
			// Read the element type as well
			getNextSy();
			elementType = MiniZincParType(newType);
			if(elementType instanceof ArrayType) {
				ArrayType arrayType = (ArrayType) elementType;
				if(arrayType.getIndexSets().size() > 1) {
					throw new MiniBrassParseException("Currently, only one-dimensional element types are supported");
				}
			}
		}
		
		newType.setElementType(elementType);
		newType.setSpecType(specType);
		
		expectSymbolAndNext(MiniBrassSymbol.RightAngleSy);		
		
		// here we can add a "represents" statement to the PVS type we embed into (e.g., Constraint Preferences represent the free PVS)
		if(currSy == MiniBrassSymbol.RepresentsSy) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.IdentSy);
			String representationType = lexer.getLastIdent();
			NamedRef<PVSType> typeRef = new NamedRef<>(representationType);
			newType.setRepresentsType(typeRef);
			semChecker.scheduleUpdate(typeRef, model.getPvsTypes());
			getNextSy();
		}
			
		expectSymbolAndNext(MiniBrassSymbol.EqualsSy);
		if(currSy == MiniBrassSymbol.ParamsSy) { // we have a parameters part
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			
			while(currSy != MiniBrassSymbol.RightCurlSy) {
				PVSFormalParameter par = parameterDecl(newType);
				
				newType.addPvsParameter(par);
			}
			expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
			expectSymbolAndNext(MiniBrassSymbol.InSy);
		} 
		
		expectSymbolAndNext(MiniBrassSymbol.InstantiatesSy);
		expectSymbolAndNext(MiniBrassSymbol.WithSy);
		expectSymbol(MiniBrassSymbol.StringLitSy);
		
		String fileName = lexer.getLastIdent();
		newType.setImplementationFile(fileName);
		
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
		while(currSy != MiniBrassSymbol.RightCurlSy) {
			mapping(newType);
		}
		getNextSy();
		
		// we can have an optional "offers" block here
		if(currSy == MiniBrassSymbol.OffersSy) {
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftCurlSy);
			
			// currently only for heuristics
			expectSymbolAndNext(MiniBrassSymbol.HeuristicsSy);
			expectSymbolAndNext(MiniBrassSymbol.ArrowSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String heuristicFunction = lexer.getLastIdent();
			newType.setOrderingHeuristic(heuristicFunction);
			
			getNextSy();

			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			expectSymbolAndNext(MiniBrassSymbol.RightCurlSy);
			
		} 
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
		
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
	private PVSFormalParameter parameterDecl(PVSType scopeType) throws MiniBrassParseException {
		PVSFormalParameter returnParameter;
		String defaultVal = null;
		WrapInformation wrapInformation = null;
		
		if(currSy == MiniBrassSymbol.ArraySy) {
			
			ArrayType arrayType = MiniZincArrayType(scopeType);
			
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String name = lexer.getLastIdent();
			getNextSy();
			
			// optional default value 
			if(currSy == MiniBrassSymbol.DoubleColonSy) {
				getNextSy();
				if(currSy == MiniBrassSymbol.DefaultSy) {
					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
					expectSymbol(MiniBrassSymbol.StringLitSy);
					defaultVal = lexer.getLastIdent();
					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
				} else {
					wrapInformation = wrappedByAnnotation(scopeType);
				}
			}
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);

			semChecker.scheduleArrayTypeCheck(arrayType, arrayType.getPendingIndexTypes(), name);
			
			// dependency checks 
			semChecker.scheduleTypeDependencyCheck(scopeType, name, arrayType.getElementType());
			for(MiniZincVarType pendingVarType : arrayType.getPendingIndexTypes()) {
				semChecker.scheduleTypeDependencyCheck(scopeType, name, pendingVarType);
			}
			
			returnParameter = new PVSFormalParameter(name, arrayType);
			
		} else {
			MiniZincVarType varType = MiniZincVarType(scopeType); // could be int
			expectSymbolAndNext(MiniBrassSymbol.ColonSy);
			expectSymbol(MiniBrassSymbol.IdentSy);
			String ident = lexer.getLastIdent();
			
			LOGGER.fine("Registering parameter "+varType + ": "+ident);
		
			getNextSy();
			
			// optional default value 
			if(currSy == MiniBrassSymbol.DoubleColonSy) {
				getNextSy();
				if(currSy == MiniBrassSymbol.DefaultSy) {
					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
					expectSymbol(MiniBrassSymbol.StringLitSy);
					defaultVal = lexer.getLastIdent();
					getNextSy();
					expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
				} else {
					wrapInformation = wrappedByAnnotation(scopeType);
				}
			}
			expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			semChecker.scheduleTypeDependencyCheck(scopeType, ident, varType);
			returnParameter = new PVSFormalParameter(ident, varType);
		}
		if(defaultVal != null) {
			returnParameter.setDefaultValue(defaultVal);
		}
		
		if(wrapInformation != null) {
			returnParameter.setWrappedBy(wrapInformation);
		}
		return returnParameter;
	}

	private WrapInformation wrappedByAnnotation(PVSType scopeType) throws MiniBrassParseException {
		WrapInformation wi = new WrapInformation();
		expectSymbolAndNext(MiniBrassSymbol.WrappedBySy);
		expectSymbolAndNext(MiniBrassSymbol.LeftParenSy);
		expectSymbol(MiniBrassSymbol.StringLitSy);
		// this could actually be just the identifier for the language 
		wi.wrapLanguage = lexer.getLastIdent();
		getNextSy();
		if(currSy == MiniBrassSymbol.CommaSy) {
			getNextSy();
			expectSymbol(MiniBrassSymbol.StringLitSy);
			wi.wrapFunction = lexer.getLastIdent();
			getNextSy();
		} else {
			wi.wrapFunction = wi.wrapLanguage;
			wi.wrapLanguage = WrapInformation.MINIZINC;						
		}
		expectSymbolAndNext(MiniBrassSymbol.RightParenSy);
		if( ! WrapInformation.MINIZINC.equals(wi.wrapLanguage) && ! WrapInformation.JAVA.equals(wi.wrapLanguage))
			throw new MiniBrassParseException("Unrecognized preprocessing (wrappedBy) language: "+wi.wrapLanguage);
		
		if(WrapInformation.JAVA.equals(wi.wrapLanguage)) {
			try {
				@SuppressWarnings("unchecked")
				Class<? extends ExternalParameterWrap> externalParameterWrap = (Class<? extends ExternalParameterWrap>) Class.forName(wi.wrapFunction);
				ExternalParameterWrap epw = externalParameterWrap.newInstance();
				wi.setExternalWrap(epw);
			} catch (ClassNotFoundException e) {
				throw new MiniBrassParseException("Class "+wi.wrapFunction + " in wrappedBy annotation was not found!");
			} catch (InstantiationException e) {
				throw new MiniBrassParseException(e);
			} catch (IllegalAccessException e) {
				throw new MiniBrassParseException(e);
			}
		}
		return wi;
	}

	private ArrayType MiniZincArrayType(PVSType scopeType) throws MiniBrassParseException {
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);
		
		ArrayType arrayType = new ArrayType();
		MiniZincVarType indexType = MiniZincVarType(scopeType);
		
		List<MiniZincVarType> pendingIndexTypes = new LinkedList<>();
		pendingIndexTypes.add(indexType);
		
		while(currSy != MiniBrassSymbol.RightBracketSy) { 
			expectSymbolAndNext(MiniBrassSymbol.CommaSy);
			
			indexType = MiniZincVarType(scopeType);
			LOGGER.fine("Next index: "+indexType);
			pendingIndexTypes.add(indexType);				
		}
		
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.OfSy);
		MiniZincVarType varType = MiniZincVarType(scopeType);
		
		arrayType.setElementType(varType);
		arrayType.setPendingIndexTypes(pendingIndexTypes);
		return arrayType;
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
				LOGGER.fine("Read verbatim: "+targetFunction);
				getNextSy();
				expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
			} else {
				getNextSy();
				expectSymbol(MiniBrassSymbol.IdentSy);
				targetFunction = lexer.getLastIdent();
				LOGGER.fine("Read function: "+targetFunction);
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
	private MiniZincParType MiniZincParType(PVSType scopeType) throws MiniBrassParseException {
		if(currSy == MiniBrassSymbol.ArraySy) {
			
			ArrayType arrayType = MiniZincArrayType(scopeType);
			semChecker.scheduleArrayTypeCheck(arrayType, arrayType.getPendingIndexTypes(), scopeType.getName());
			
			// dependency checks 
			semChecker.scheduleTypeDependencyCheck(scopeType, scopeType.getName(), arrayType.getElementType());
			for(MiniZincVarType pendingVarType : arrayType.getPendingIndexTypes()) {
				semChecker.scheduleTypeDependencyCheck(scopeType, scopeType.getName(), pendingVarType);
			}

			return arrayType;
		} else if (currSy == MiniBrassSymbol.MSetSy) {
			getNextSy();
			expectSymbolAndNext(MiniBrassSymbol.LeftBracketSy);
			NumericValue maxMultiplicity = getNumericExpr(scopeType);
			expectSymbolAndNext(MiniBrassSymbol.RightBracketSy);
			expectSymbol(MiniBrassSymbol.OfSy);
			getNextSy();
			PrimitiveType pt = primType(scopeType);
			return new MultiSetType(maxMultiplicity, pt);
		}
		else {
			return MiniZincVarType(scopeType);
		}
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
		} 
		else {
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
	 * @param model2 
	 * @throws MiniBrassParseException 
	 */
	private void includeItem(MiniBrassAST model) throws MiniBrassParseException {
		expectSymbol(MiniBrassSymbol.StringLitSy);
		// add this file to our work list
		String fileName = lexer.getLastIdent();

		if(fileName.endsWith(".mzn")) {
			model.getAdditionalMinizincIncludes().add(fileName);
		} else {
			File referred = new File(currDir,fileName);
			if(!referred.exists())  {
				if(mbrStdDir != null) {
					referred = new File(mbrStdDir, fileName);
					if(!referred.exists())
						throw new MiniBrassParseException("Could not find file "+fileName + " in either working directory or MiniBrass standard lib");
				}
			}
			
			if(!visited.contains(referred)) {
				try {
					worklistOfStreams.add(new FileInputStream(referred));
					visited.add(referred);
				} catch (FileNotFoundException e) {
					throw new MiniBrassParseException(e);
				}
			}
		}
		getNextSy();
		expectSymbolAndNext(MiniBrassSymbol.SemicolonSy);
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

	public MiniBrassAST getLastModel() {
		return model;
	}


}

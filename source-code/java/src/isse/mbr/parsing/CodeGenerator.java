package isse.mbr.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import isse.mbr.extensions.ExternalMorphism;
import isse.mbr.extensions.ExternalParameterWrap;
import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.MorphedPVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.parsetree.ProductType;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.parsetree.SoftConstraint;
import isse.mbr.model.parsetree.VotingInstance;
import isse.mbr.model.types.ArrayType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincArrayLike;
import isse.mbr.model.types.MiniZincParType;
import isse.mbr.model.types.MiniZincVarType;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PVSFormalParameter;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSType;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.tools.SolutionRecorder.Solution;

/**
 * Creates a MiniZinc file that contains all predicates, variables etc. that are
 * needed for minisearch
 * 
 * @author Alexander Schiendorfer
 *
 */
public class CodeGenerator {

	private final static Logger LOGGER = Logger.getGlobal();

	private static final String OVERALL_KEY = "overall"; 
	private static final String VALUATIONS_KEY = "valuations"; 
	private static final String TOP_KEY = "top";
	private static final String MBR_PREFIX = "mbr.";
	public static final String VALUATTIONS_PREFIX = "Valuations:";
	public static final String INTERMEDIATE_SOLUTIONS_PREFIX = "Intermediate solution:";
	public static final String SEARCH_HEURISTIC_KEY = "searchHeuristic";
	public static final String ITER_VARIABLE_PREFIX = "mbr_iter_var_";
	public static final String AUX_VARIABLE_PREFIX = "mbr_aux_var_";

	private boolean onlyMiniZinc;
	private boolean genHeuristics;
	private boolean suppressOutputGeneration = false; // this should not be accessible from outside, better use the new MiniBrass output item 
	private String generatedOutput; // for minisearch

	private boolean writeDecisionVariables = true;
	private boolean writeSoftConstraints = true; 
	
	private List<PVSInstance> leafInstances;
	private Collection<CodegenAtomicPVSHandler> handlers;
	
	public static class AtomicPvsInformation {
		protected PVSInstance instance;
		protected String overall;
		protected String valuationsArray;
		protected Map<String, String> substitutions;
		protected StringBuilder instanceArguments;
		
		public AtomicPvsInformation(PVSInstance instance, String overall, String valuationsArray,
				Map<String, String> substitutions, StringBuilder instanceArguments) {
			super();
			this.instance = instance;
			this.overall = overall;
			this.valuationsArray = valuationsArray;
			this.substitutions = substitutions;
			this.instanceArguments = instanceArguments;
		}

		public PVSInstance getInstance() {
			return instance;
		}

		public String getOverall() {
			return overall;
		}

		public String getValuationsArray() {
			return valuationsArray;
		}

		public Map<String, String> getSubstitutions() {
			return substitutions;
		}

		public StringBuilder getInstanceArguments() {
			return instanceArguments;
		}
		
		
	}

	public CodeGenerator() {
		this.handlers = new ArrayList<>();
	}
	
	public String generateCode(MiniBrassAST model) throws MiniBrassParseException {
		LOGGER.fine("Starting code generation");
		StringBuilder sb = new StringBuilder("% ===============================================\n");
		sb.append("% Generated code from MiniBrass, do not modify!\n");

		addIncludes(sb, model);
		addPvsInstances(sb, model);

		// only final output here
		String fileContents = sb.toString();
	
		return fileContents;

	}
	
	public String generateComparisonCode(MiniBrassAST model, Solution left, Solution right) throws MiniBrassParseException {
		LOGGER.fine("Starting comparison code generation");
		StringBuilder sb = new StringBuilder("% ===============================================\n");
		sb.append("% Generated comparison code from MiniBrass, do not modify!\n");

		addIncludes(sb, model);
		addPvsInstances(sb, model);

		// only final output here
		String fileContents = sb.toString();
	
		return fileContents;		
	}

	private void addIncludes(StringBuilder sb, MiniBrassAST model) {
		Set<String> addedFiles = new HashSet<>();
		for (Entry<String, PVSType> pvsType : model.getPvsTypes().entrySet()) {
			String referencedFile = pvsType.getValue().getImplementationFile();
			if (!addedFiles.contains(referencedFile)) {
				sb.append(String.format("include \"%s\";\n", referencedFile));
				addedFiles.add(referencedFile);
			}
		}

		// additional minizinc files specified in minibrass file
		for (String addMinizincFile : model.getAdditionalMinizincIncludes()) {
			sb.append(String.format("include \"%s\";\n", addMinizincFile));
		}
	}

	private void addPvsInstances(StringBuilder sb, MiniBrassAST model) throws MiniBrassParseException {
		// start with solve item then continue only its referenced instances
		// (that can play an active role)
		AbstractPVSInstance topLevelInstance = deref(model.getSolveInstance());
		addExportedOverallMiniZincHooks(sb, model, topLevelInstance);

		leafInstances = new LinkedList<PVSInstance>();

		// here the actual recursive code generation takes place
		addPvs(topLevelInstance, sb, model);

		// get the improvement predicates
		if (!onlyMiniZinc) {
			String pvsBetter = encodeString(MiniZincKeywords.POST_GET_BETTER, topLevelInstance);
			String pvsNotWorse = encodeString(MiniZincKeywords.POST_NOT_GET_WORSE, topLevelInstance);
			sb.append(String.format("\nfunction ann: %s() = post(%s);\n", pvsBetter,
					topLevelInstance.getGeneratedBetterPredicate()));

			StringBuilder equalityBuilder = new StringBuilder();
			boolean firstLeaf = true;
			for (PVSInstance leafInstance : leafInstances) {
				String leafValuation = getOverallValuation(leafInstance);
				if (firstLeaf)
					firstLeaf = false;
				else
					equalityBuilder.append(" /\\ ");
				// now this must be available for arrays as well
				if (leafInstance.getType().instance.getElementType() instanceof MiniZincArrayLike) {
					ArrayType at = ((MiniZincArrayLike) leafInstance.getType().instance.getElementType())
							.getArrayType();
					String comprehensionString = getComprehensionString(at, leafInstance);
					String indexString = getIndexString(at, leafInstance);
					String equalArrays = String.format(
							"[ sol(%s[" + indexString + "]) | " + comprehensionString + "] = %s ", leafValuation,
							leafValuation);
					equalityBuilder.append(equalArrays);
				} else {
					equalityBuilder.append(String.format("sol(%s) = %s", leafValuation, leafValuation));
				}
			}

			String negatedGetNotWorse = "not ( (" + equalityBuilder.toString() + ") \\/ ("
					+ topLevelInstance.getGeneratedNotWorsePredicate() + "))";
			sb.append(String.format("\nfunction ann: %s() = post(%s);\n", pvsNotWorse, negatedGetNotWorse));
		}

		// add output line for valuation-carrying variables
		
		StringBuilder outputBuilder = new StringBuilder();
		if(model.getProblemOutput() != null)
			outputBuilder.append(model.getProblemOutput() + " ++ ");
		
		outputBuilder.append("[ \"\\n" + VALUATTIONS_PREFIX + " ");
		boolean first = true;
		for (PVSInstance leafInstance : leafInstances) {
			String val = getOverallValuation(leafInstance);
			if (!first)
				outputBuilder.append("; ");
			else
				first = false;
			outputBuilder.append(String.format("%s = \\(%s)", val, val));
		}
		outputBuilder.append("\\n\"]");
		generatedOutput = "output "+ outputBuilder.toString() + ";\n";
		if(!suppressOutputGeneration)
			sb.append(generatedOutput+"\n");
	}

	/**
	 * The overall exported MiniZinc hooks are those keywords that can
	 * be easily accessed in an output statement and/or for search ("postGetBetter", "topLevelObjective", ...)
	 * @param sb
	 * @param model
	 * @param topLevelInstance
	 */
	private void addExportedOverallMiniZincHooks(StringBuilder sb, MiniBrassAST model,
			AbstractPVSInstance topLevelInstance) {
		sb.append("\n% ---------------------------------------------------");
		sb.append("\n% Overall exported predicate (and objective in case of atomic top-level PVS) : \n");
		sb.append("\n% ---------------------------------------------------\n");

		String pvsBetter = encodeString(MiniZincKeywords.POST_GET_BETTER, topLevelInstance);
		String pvsNotWorse = encodeString(MiniZincKeywords.POST_NOT_GET_WORSE, topLevelInstance);

		if (!onlyMiniZinc) {
			sb.append(String.format("function ann:  " + MiniZincKeywords.POST_GET_BETTER + "() = %s();\n", pvsBetter));
			sb.append(String.format("function ann:  " + MiniZincKeywords.POST_NOT_GET_WORSE + "() = %s();\n",
					pvsNotWorse));
		}

		// there could be a numeric objective in a voting-PVS
		String topLevelOverall = null;
		MiniZincParType elementType = null;
		
		if(topLevelInstance instanceof VotingInstance) {
			VotingInstance vi = (VotingInstance) topLevelInstance;
			if(vi.getVotingProcedure().hasNumericObjective()) {
				topLevelOverall = getOverallValuation(topLevelInstance);
				elementType = vi.getVotingProcedure().getObjectiveType(this, vi.getChildren());
			
			}
		}
			
		if (!(topLevelInstance.isComplex())) {
			topLevelOverall = getOverallValuation(topLevelInstance);
			PVSInstance pvsInst = (PVSInstance) topLevelInstance;
			elementType = pvsInst.getType().instance.getElementType();
		}
		
		if(topLevelOverall != null) { // either case fits
			appendOverall(sb, elementType, topLevelInstance, MiniZincKeywords.TOP_LEVEL_OBJECTIVE);
			sb.append(String.format("constraint %s = %s;\n", MiniZincKeywords.TOP_LEVEL_OBJECTIVE, topLevelOverall));		
		}
	
			
		if (genHeuristics)
			sb.append("ann: " + MiniZincKeywords.PVS_SEARCH_HEURISTIC + " = "
					+ CodeGenerator.encodeString(SEARCH_HEURISTIC_KEY, topLevelInstance) + ";\n");

	}

	private void addPvs(AbstractPVSInstance pvsInstance, StringBuilder sb, MiniBrassAST model)
			throws MiniBrassParseException {
		if (pvsInstance.isComplex()) {

			Collection<AbstractPVSInstance> children = pvsInstance.getChildren();
			for (AbstractPVSInstance apvs : children) {
				AbstractPVSInstance child = deref(apvs);
				addPvs(child, sb, model);
			}

			// search heuristics as well
			if (genHeuristics) {
				sb.append("\n% Complex search Heuristics to be used in a model: \n");

				// String annDecl = "ann:
				// "+CodeGenerator.encodeString(SEARCH_HEURISTIC_KEY,
				// pvsInstance) + String.format(" = seq_search( [%s,
				// %s]);",leftHeur,rightHeur);
				String annDecl = "ann: " + CodeGenerator.encodeString(SEARCH_HEURISTIC_KEY, pvsInstance)
						+ ( " = seq_search( [" );

				sb.append(annDecl);
				sb.append('\n');
				boolean first = true;
				for (AbstractPVSInstance apvs : children) {
					if (first)
						first = false;
					else
						sb.append(", ");
					AbstractPVSInstance child = deref(apvs);
					String heur = CodeGenerator.encodeString(SEARCH_HEURISTIC_KEY, child);
					sb.append(heur);
				}
				sb.append("]);");

			}

			if (pvsInstance instanceof CompositePVSInstance) {
				CompositePVSInstance comp = (CompositePVSInstance) pvsInstance;
				AbstractPVSInstance left = deref(comp.getLeftHandSide());
				AbstractPVSInstance right = deref(comp.getRightHandSide());

				String equalsLeft = left.getGeneratedEqualsPredicate();
				String equalsRight = right.getGeneratedEqualsPredicate();

				// equals
				String generatedEqualsPredicate = String.format("(%s) /\\ (%s)", equalsLeft, equalsRight);
				comp.setGeneratedEqualsPredicate(generatedEqualsPredicate);
				
				if (comp.getProductType() == ProductType.DIRECT) {
					String leftBetter = left.getGeneratedBetterPredicate();
					String rightBetter = right.getGeneratedBetterPredicate();
					comp.setGeneratedBetterPredicate(String.format("( (%s) /\\ (%s) )", leftBetter, rightBetter));
					comp.setGeneratedNotWorsePredicate(String.format("( (%s) /\\ (%s) )", rightBetter, leftBetter));
				} else { // lexicographic
					String leftBetter = left.getGeneratedBetterPredicate();
					String rightBetter = right.getGeneratedBetterPredicate();
					
					comp.setGeneratedBetterPredicate(String.format("( (%s) \\/ ( (%s) /\\ %s) )", leftBetter, equalsLeft, rightBetter));
					comp.setGeneratedNotWorsePredicate(String.format("( (%s) \\/ ( (%s) /\\ %s) )", rightBetter, equalsRight, leftBetter));
				}
				
			} else { // has to be vote instance
				VotingInstance vi = (VotingInstance) pvsInstance;
				String getBetterPredicate = vi.getVotingProcedure().getVotingPredicate(this, vi.getChildren());
				vi.setGeneratedBetterPredicate(getBetterPredicate);
				// TODO implement more reasonably 
				vi.setGeneratedNotWorsePredicate("true");
				StringBuilder equalityBuilder = new StringBuilder("(");
				boolean f = true;
				for(AbstractPVSInstance voter : vi.getChildren()) {
					voter = ReferencedPVSInstance.deref(voter);
					if (f)
						f = false;
					else 
						equalityBuilder.append(" /\\ ");
					
					equalityBuilder.append(String.format("(%s)", voter.getGeneratedEqualsPredicate()));
				}
				equalityBuilder.append(")");
				vi.setGeneratedEqualsPredicate(equalityBuilder.toString());
				
				if(vi.getVotingProcedure().hasNumericObjective()) {
					String overallIdent = getOverallValuation(vi);
					appendOverall(sb, vi.getVotingProcedure().getObjectiveType(this, vi.getChildren()), vi, overallIdent);
					sb.append(String.format("constraint %s = %s ;\n", overallIdent, vi.getVotingProcedure().getNumericObjective(this, vi.getChildren())));
				}
			}

		} else {
			addAtomicPvs(pvsInstance, sb, model);
		}

	}

	/**
	 * Responsible for adding a single, atomic PVS along with parameters, decision variables, and minisearch predicates
	 * @param pvsInstance
	 * @param sb
	 * @param model
	 * @throws MiniBrassParseException
	 */
	private void addAtomicPvs(AbstractPVSInstance pvsInstance, StringBuilder sb, MiniBrassAST model)
			throws MiniBrassParseException {
		// some objects referenced by many sub-methods
		PVSInstance inst = (PVSInstance) pvsInstance;
		leafInstances.add(inst);
		PVSType pvsType = inst.getType().instance;
		String overallIdent = getOverallValuation(inst);
		String valuationsArray = CodeGenerator.encodeString(VALUATIONS_KEY, inst);
		
		// first the parameters of the atomic PVS instance
		Map<String, String> subs = addAtomicPvsParameters(inst, sb, model);
		StringBuilder instanceArguments = getInstanceArguments(pvsType, inst);
		
		AtomicPvsInformation api = new AtomicPvsInformation(inst, overallIdent, valuationsArray, subs, instanceArguments);

		// -------------------------------------------------------------
		// Decision variables
		if(writeDecisionVariables)
			addDecisionVariables(inst, sb, model, subs, overallIdent, valuationsArray, instanceArguments);		
		
		// -------------------------------------------------------------
		// MiniSearch predicates variables
		if(!onlyMiniZinc)
			addMiniSearchPredicates(inst, sb, model, subs, overallIdent, instanceArguments);
		
		// -------------------------------------------------------------
		// Soft constraints
		if(writeSoftConstraints)
			addSoftConstraints(inst, sb, model, subs, valuationsArray);
	
		// -------------------------------------------------------------
		// PVS-specific search heuristics
		if (genHeuristics) 
			addHeuristics(inst, sb, model,  overallIdent, instanceArguments, valuationsArray);
		
		// -------------------------------------------------------------
		// Additional handlers (needed, e.g., for pairwise comparisons)
		for(CodegenAtomicPVSHandler handler : handlers) {
			handler.handleAtomicPvs(api, model, sb);
		}

	}

	/**
	 * Problem specific heuristics such as "most-important first" written as an annotation, basically only links to a corresponding MiniZinc function
	 * @param inst
	 * @param sb
	 * @param model
	 * @param overallIdent
	 * @param instanceArguments
	 * @param valuationsArray
	 */
	private void addHeuristics(PVSInstance inst, StringBuilder sb, MiniBrassAST model, String overallIdent, StringBuilder instanceArguments, String valuationsArray) {	
		sb.append("\n% Search Heuristics to be used in a model: \n");
		String heuristicFunc = inst.getType().instance.getOrderingHeuristic();

		String annDecl = "ann: " + CodeGenerator.encodeString(SEARCH_HEURISTIC_KEY, inst);
		sb.append(annDecl);
		if (heuristicFunc != null) {
			sb.append(" = ");
			sb.append(String.format("%s(%s, %s, %s);\n", heuristicFunc, valuationsArray, overallIdent,
					instanceArguments.toString()));
		} else {
			sb.append(";\n");
		}

	}

	/**
	 * The soft constraints (EXPR) specified in MiniBrass are translated into the form "valuations[i] = EXPR"
	 * @param inst
	 * @param sb
	 * @param model
	 * @param subs
	 * @param valuationsArray
	 */
	private void addSoftConstraints(PVSInstance inst, StringBuilder sb, MiniBrassAST model, Map<String, String> subs,
			String valuationsArray) {
		sb.append("\n% Soft constraints: \n");
		PVSType pvsType = inst.getType().instance;
		
		for (SoftConstraint sc : inst.getSoftConstraints().values()) {
			if (pvsType.getElementType() instanceof MiniZincArrayLike) {
				MiniZincArrayLike mal = (MiniZincArrayLike) pvsType.getElementType();
				ArrayType at = mal.getArrayType();
				String arrayDecisionVariable = getArrayDecisionVariable(pvsType.getElementType(), inst,
						AUX_VARIABLE_PREFIX + sc.getId());
				String auxIdent = AUX_VARIABLE_PREFIX + sc.getId();
				sb.append(arrayDecisionVariable + " = " + CodeGenerator.processSubstitutions(sc.getMznLiteral(), subs)
						+ ";\n");
				String indexString = getIndexString(at, inst);
				String comprehensions = getComprehensionString(at, inst);
				sb.append(String.format("constraint forall(%s) ( %s[%d,%s] = %s[%s] );\n\n", comprehensions,
						valuationsArray, sc.getId(), indexString, auxIdent, indexString));
			} else {
				sb.append(String.format("constraint %s[%d] = (%s);\n", valuationsArray, sc.getId(),
						CodeGenerator.processSubstitutions(sc.getMznLiteral(), subs)));
			}

		}
		
	}

	/**
	 * We write two MiniSearch predicates, one requiring to "get better" for domination-based search and one "not get worse" for "only not be dominated" search (i.e., to find all optima)
	 * Not be dominated requires negation so the predicate better be applicable in a negated context (no free local variables such as in SPD for deciding the witness)
	 * @param inst
	 * @param sb
	 * @param model
	 * @param subs
	 * @param overallIdent
	 * @param instanceArguments
	 */
	private void addMiniSearchPredicates(PVSInstance inst, StringBuilder sb, MiniBrassAST model, Map<String, String> subs, String overallIdent, StringBuilder instanceArguments) {
		sb.append("\n% MiniSearch predicates: \n");
		PVSType pvsType = inst.getType().instance;
		//
		String lastSolutionDegree = String.format("sol(%s)", overallIdent);
		if (pvsType.getElementType() instanceof MiniZincArrayLike) {
			String comprehensionString = getComprehensionString(
					((MiniZincArrayLike) pvsType.getElementType()).getArrayType(), inst);
			String indexString = getIndexString(((MiniZincArrayLike) pvsType.getElementType()).getArrayType(), inst);
			lastSolutionDegree = String.format("[ sol(%s[" + indexString + "]) | " + comprehensionString + "]",
					overallIdent);
		}
		String getBetterString = String.format("%s(%s, %s, %s)", pvsType.getOrder(), lastSolutionDegree, overallIdent,
				instanceArguments.toString());
		String notGetWorseString = String.format("%s(%s, %s, %s)", pvsType.getOrder(), overallIdent, lastSolutionDegree,
				instanceArguments.toString());
		String equalityString = String.format("sol(%s) = %s", overallIdent, overallIdent);

		inst.setGeneratedBetterPredicate(getBetterString);
		inst.setGeneratedNotWorsePredicate(notGetWorseString);
		inst.setGeneratedEqualsPredicate(equalityString);
	}

	/**
	 * The decision variables hold the valuations of each soft constraint and the overall combination using the times operation 
	 * @param inst
	 * @param sb
	 * @param model
	 * @param subs
	 * @param overallIdent
	 * @param valuationsArray
	 * @param instanceArguments
	 */
	private void addDecisionVariables(PVSInstance inst, StringBuilder sb, MiniBrassAST model, Map<String, String> subs, String overallIdent, String valuationsArray, StringBuilder instanceArguments) {
		sb.append("\n% Decision variables: \n");
		PVSType pvsType = inst.getType().instance;
		
		appendOverall(sb, pvsType.getElementType(), inst, overallIdent);

		PVSFormalParameter numberSoftConstraints = pvsType.getParamMap().get(PVSType.N_SCS_LIT);
		IntervalType softConstraintsSet = new IntervalType(new NumericValue(1), new NumericValue(numberSoftConstraints));

		
		MiniZincParType specType = pvsType.getSpecType();
		if (specType instanceof MiniZincVarType) {
			sb.append(String.format("array[%s] of var %s: %s;\n", softConstraintsSet.toMzn(inst),
					encode(pvsType.getSpecType(), inst), valuationsArray));
		} else {
			MiniZincArrayLike arrayLike = (MiniZincArrayLike) specType;
			ArrayType arrayType = arrayLike.getArrayType();
			sb.append(String.format("array[%s,%s] of var %s: %s;\n", softConstraintsSet.toMzn(inst),
					getArrayIndexSets(arrayType, inst), encode(arrayType.getElementType(), inst), valuationsArray));
		}
		String topIdent = CodeGenerator.encodeString(TOP_KEY, inst);
		// String topElement = CodeGenerator.encodeString( pvsType.getTop(), inst);
		String topElement = CodeGenerator.processSubstitutions(pvsType.getTop(), subs);
		
		sb.append(String.format("%s: %s = %s;\n", encode(pvsType.getElementType(), inst), topIdent, topElement));
		
		sb.append(String.format("constraint %s = %s (%s,%s);\n", overallIdent, pvsType.getCombination(),
				valuationsArray, instanceArguments.toString()));
	}

	/**
	 * The parameters are generated as MiniZinc parameters and passed to each application of an ordering predicate etc
	 * @param inst
	 * @param sb
	 * @param model
	 * @return
	 * @throws MiniBrassParseException
	 */
	private Map<String, String> addAtomicPvsParameters(PVSInstance inst, StringBuilder sb, MiniBrassAST model) throws MiniBrassParseException {
		
		ExternalMorphism em = null;
		if (inst instanceof MorphedPVSInstance) {
			MorphedPVSInstance minst = (MorphedPVSInstance) inst;
			StringBuilder fromArguments = getInstanceArguments(minst.getMorphism().instance.getFrom().instance, inst);
			minst.update(fromArguments);
			em = minst.getMorphism().instance.getExternalMorphism();
			if (em != null)
				em.setFromInstance(inst);
		}
		String name = inst.getName();

		sb.append("\n% ---------------------------------------------------");
		sb.append("\n%   PVS " + name);
		sb.append("\n% ---------------------------------------------------\n");
		
		// first the parameters
		sb.append("% Parameters: \n");

		// prepare possible substitutions
		Map<String, String> subs = prepareSubstitutions(inst);

		// I actually want to go through every PVSParam and look for the proper
		// inst

		for (PVSFormalParameter pvsParam : inst.getInstanceParameters()) {
			PVSParamInst pi = inst.getCheckedParameters().get(pvsParam.getName());

			// is it an array type (important for annotations) or not?
			String paramExpression = null;
			String defaultValue = pvsParam.getDefaultValue();

			if (pvsParam.getType() instanceof ArrayType) {
				if (pi == null) { // we need to collect the values from the
									// individual soft constraints
					StringBuilder exprBuilder = new StringBuilder("[");
					boolean first = true;

					for (SoftConstraint sc : inst.getSoftConstraints().values()) {
						String annotValue = sc.getAnnotations().get(pvsParam.getName());
						if (annotValue == null)
							annotValue = defaultValue;
						if (first)
							first = false;
						else
							exprBuilder.append(", ");
						exprBuilder.append(annotValue);
					}
					exprBuilder.append("]");
					paramExpression = exprBuilder.toString();
				} else {
					if (!pi.generated) {
						paramExpression = pi.expression;
					} else {
						pi.expression = em.getParameterString(pvsParam);
						paramExpression = pi.expression;
					}
				}
			} else {
				if (pi != null) {
					if (!pi.generated)
						paramExpression = pi.expression;
					else {
						pi.expression = em.getParameterString(pvsParam);
						paramExpression = pi.expression;
					}
				} else
					paramExpression = defaultValue;
			}
			if (pvsParam.getWrappedBy() != null) {
				if (WrapInformation.MINIZINC.equals(pvsParam.getWrappedBy().wrapLanguage)) {
					paramExpression = String.format("%s(%s)", pvsParam.getWrappedBy().wrapFunction, paramExpression);
				}
				if (WrapInformation.JAVA.equals(pvsParam.getWrappedBy().wrapLanguage)) {
					ExternalParameterWrap epw = pvsParam.getWrappedBy().getExternalWrap();
					paramExpression = epw.process(inst, pvsParam,
							CodeGenerator.processSubstitutions(paramExpression, subs));
				}
			}
			String generatedParamExpression = CodeGenerator.processSubstitutions(paramExpression, subs);
			inst.getGeneratedCodeParameters().put(pvsParam.getName(), generatedParamExpression);
			String def = String.format("%s : %s = %s; \n", encode(pvsParam.getType(), inst),
					CodeGenerator.encodeIdent(pvsParam, inst), generatedParamExpression);
			sb.append(def);
		}
		return subs;
	}

	private String getIndexString(ArrayType arrayType, PVSInstance inst) {
		StringBuilder sbBuilder = new StringBuilder();

		for (int i = 0; i < arrayType.getIndexSets().size(); ++i) {
			if (i > 0)
				sbBuilder.append(",");
			sbBuilder.append(ITER_VARIABLE_PREFIX + i);
			++i;
		}
		return sbBuilder.toString();
	}

	private String getComprehensionString(ArrayType arrayType, PVSInstance inst) {
		StringBuilder sbBuilder = new StringBuilder();
		int i = 0;
		for (PrimitiveType index : arrayType.getIndexSets()) {
			sbBuilder.append(String.format("%s in %s", ITER_VARIABLE_PREFIX + i, encode(index, inst)));
			++i;
		}
		return sbBuilder.toString();
	}

	private void appendOverall(StringBuilder sb, MiniZincParType elementType, AbstractPVSInstance inst, String overallIdent) {
		if (elementType instanceof MiniZincVarType) {
			sb.append(String.format("var %s: %s;\n", encode(elementType, inst), overallIdent));
		} else { // must be MiniZincArrayLike then
			String arrayDecisionVariable = getArrayDecisionVariable(elementType, inst, overallIdent);
			sb.append(arrayDecisionVariable + ";\n");
		}
	}

	private String getArrayDecisionVariable(MiniZincParType elementType, AbstractPVSInstance inst, String arrayIdent) {
		MiniZincArrayLike miniZincArrayLike = (MiniZincArrayLike) elementType;
		ArrayType arrayType = miniZincArrayLike.getArrayType();
		StringBuilder sb = new StringBuilder();

		sb.append("array[");
		sb.append(getArrayIndexSets(arrayType, inst));
		sb.append(String.format("] of var %s: %s", encode(arrayType.getElementType(), inst), arrayIdent));
		return sb.toString();
	}

	private String getArrayIndexSets(ArrayType arrayType, AbstractPVSInstance inst) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (PrimitiveType pt : arrayType.getIndexSets()) {
			if (first)
				first = false;
			else
				sb.append(",");
			sb.append(encode(pt, inst));
		}
		return sb.toString();
	}

	private StringBuilder getInstanceArguments(PVSType pvsType, PVSInstance inst) {
		StringBuilder instanceArguments = new StringBuilder();
		boolean first = true;

		for (PVSFormalParameter pvsParam : pvsType.getFormalParameters()) {
			if (!first)
				instanceArguments.append(", ");
			else
				first = false;
			String parIdent = CodeGenerator.encodeIdent(pvsParam, inst);
			instanceArguments.append(parIdent);
		}
		return instanceArguments;
	}

	private Map<String, String> prepareSubstitutions(PVSInstance inst) {
		HashMap<String, String> substitutions = new HashMap<>();
		List<String> keys = new ArrayList<>();

		// all soft constraints map to their id
		for (SoftConstraint sc : inst.getSoftConstraints().values()) {
			substitutions.put(MBR_PREFIX + sc.getName(), Integer.toString(sc.getId()));
			keys.add(sc.getName());
		}

		// all parameters to their encoded id
		for(PVSFormalParameter parameter : inst.getType().instance.getFormalParameters()) {
			String encodedIdent = CodeGenerator.encodeIdent(parameter, inst);
			substitutions.put(MBR_PREFIX + parameter.getName(), encodedIdent);
			keys.add(parameter.getName());
		}
		LinkedHashMap<String, String> subs = new LinkedHashMap<>();
		Collections.sort(keys, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return Integer.compare(arg1.length(), arg0.length());
			}
		});
		for (String s : keys) {
			String prefixed = MBR_PREFIX + s;
			subs.put(prefixed, substitutions.get(prefixed));
		}
		return subs;
	}

	private static String processSubstitutions(String expression, Map<String, String> subs) {
		for (Entry<String, String> entry : subs.entrySet()) {
			if (expression.contains(entry.getKey())) {
				expression = expression.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return expression;
	}
	
	public void addAtomicPVSHandler(CodegenAtomicPVSHandler handler) {
		handlers.add(handler);
	}

	public String getOverallValuation(AbstractPVSInstance inst) {
		return encodeString(OVERALL_KEY, inst);
	}

	public String getTopValue(AbstractPVSInstance inst) {
		return encodeString(TOP_KEY, inst);
	}
	
	public AbstractPVSInstance deref(AbstractPVSInstance pvsInstance) {
		return ReferencedPVSInstance.deref(pvsInstance);
	}

	public static String encodeString(String string, String instName) {
		return "mbr_" + string + "_" + instName;
	}

	public static String encodeString(String string, AbstractPVSInstance inst) {
		return encodeString(string, inst.getName());
	}

	private String encode(MiniZincParType type, AbstractPVSInstance concreteInstance) {
		return type.toMzn(concreteInstance);
	}

	public static String encodeIdent(PVSFormalParameter par, AbstractPVSInstance instance) {
		return encodeString(par.getName(), instance);
	}

	public boolean isOnlyMiniZinc() {
		return onlyMiniZinc;
	}

	public void setOnlyMiniZinc(boolean onlyMiniZinc) {
		this.onlyMiniZinc = onlyMiniZinc;
	}

	public boolean isGenHeuristics() {
		return genHeuristics;
	}

	public void setGenHeuristics(boolean genHeuristics) {
		this.genHeuristics = genHeuristics;
	}

	public boolean isSuppressOutputGeneration() {
		return suppressOutputGeneration;
	}

	public void setSuppressOutputGeneration(boolean suppressOutputGeneration) {
		this.suppressOutputGeneration = suppressOutputGeneration;
	}

	public boolean isWriteDecisionVariables() {
		return writeDecisionVariables;
	}

	public void setWriteDecisionVariables(boolean writeDecisionVariables) {
		this.writeDecisionVariables = writeDecisionVariables;
	}

	public boolean isWriteSoftConstraints() {
		return writeSoftConstraints;
	}

	public void setWriteSoftConstraints(boolean writeSoftConstraints) {
		this.writeSoftConstraints = writeSoftConstraints;
	}
}

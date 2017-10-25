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
	private static final String MBR_PREFIX = "mbr.";
	public static final String VALUATTIONS_PREFIX = "Valuations:";
	public static final String SEARCH_HEURISTIC_KEY = "searchHeuristic";
	public static final String ITER_VARIABLE_PREFIX = "mbr_iter_var_";
	public static final String AUX_VARIABLE_PREFIX = "mbr_aux_var_";

	private boolean onlyMiniZinc;
	private boolean genHeuristics;

	private List<PVSInstance> leafInstances;

	public String generateCode(MiniBrassAST model) throws MiniBrassParseException {
		LOGGER.fine("Starting code generation");
		// for now, just fill a string builder and print the console
		StringBuilder sb = new StringBuilder("% ===============================================\n");
		sb.append("% Generated code from MiniBrass, do not modify!\n");

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

		leafInstances = new LinkedList<>();

		// here the actual recursive code generation takes place
		addPvs(deref(topLevelInstance), sb, model);

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
		sb.append("\n% Add this line to your output to make use of minisearch\n");
		sb.append("% [ \"\\n" + VALUATTIONS_PREFIX + " ");
		boolean first = true;
		for (PVSInstance leafInstance : leafInstances) {
			String val = getOverallValuation(leafInstance);
			if (!first)
				sb.append("; ");
			else
				first = false;
			sb.append(String.format("%s = \\(%s)", val, val));
		}
		sb.append("\\n\"]\n");
	}

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

		if (!(topLevelInstance.isComplex())) {
			String topLevelOverall = getOverallValuation(topLevelInstance);
			PVSInstance pvsInst = (PVSInstance) topLevelInstance;
			MiniZincParType elementType = pvsInst.getType().instance.getElementType();
			appendOverall(sb, elementType, pvsInst, MiniZincKeywords.TOP_LEVEL_OBJECTIVE);
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
						+ (" = seq_search( [");

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

				if (comp.getProductType() == ProductType.DIRECT) {
					String leftBetter = left.getGeneratedBetterPredicate();
					String rightBetter = right.getGeneratedBetterPredicate();
					comp.setGeneratedBetterPredicate(String.format("( (%s) /\\ (%s) )", leftBetter, rightBetter));
					comp.setGeneratedNotWorsePredicate(String.format("( (%s) /\\ (%s) )", rightBetter, leftBetter));
				} else { // lexicographic
					String leftBetter = left.getGeneratedBetterPredicate();
					String rightBetter = right.getGeneratedBetterPredicate();

					// get left objective variable
					String leftOverall = getOverallValuation(left);
					String rightOverall = getOverallValuation(right);

					comp.setGeneratedBetterPredicate(String.format("( (%s) \\/ (sol(%s) = %s /\\ %s) )", leftBetter,
							leftOverall, leftOverall, rightBetter));
					comp.setGeneratedNotWorsePredicate(String.format("( (%s) \\/ (sol(%s) = %s /\\ %s) )", rightBetter,
							rightOverall, rightOverall, leftBetter));

					// sb.append(String.format("predicate %s() = (%s() ) \\/ (
					// sol(%s) = %s /\\ %s() );\n", pvsPred, leftBetter,
					// leftOverall, leftOverall, rightBetter));
				}
			} else { // has to be vote instance
				VotingInstance vi = (VotingInstance) pvsInstance;
				String getBetterPredicate = vi.getVotingProcedure().getVotingPredicate(this, vi.getChildren());
				vi.setGeneratedBetterPredicate(getBetterPredicate);
				// TODO implement more reasonably 
				vi.setGeneratedNotWorsePredicate("true");
			}

		} else {
			addAtomicPvs(pvsInstance, sb, model);
		}

	}

	private void addAtomicPvs(AbstractPVSInstance pvsInstance, StringBuilder sb, MiniBrassAST model)
			throws MiniBrassParseException {
		PVSInstance inst = (PVSInstance) pvsInstance;
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
		PVSType pvsType = inst.getType().instance;
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

		StringBuilder instanceArguments = getInstanceArguments(pvsType, inst);

		// -------------------------------------------------------------
		sb.append("\n% Decision variables: \n");

		String overallIdent = getOverallValuation(inst);
		leafInstances.add(inst);

		appendOverall(sb, pvsType.getElementType(), inst, overallIdent);

		PVSFormalParameter nScs = pvsType.getParamMap().get(PVSType.N_SCS_LIT);
		IntervalType sCSet = new IntervalType(new NumericValue(1), new NumericValue(nScs));

		String valuationsArray = CodeGenerator.encodeString("valuations", inst);

		MiniZincParType specType = pvsType.getSpecType();
		if (specType instanceof MiniZincVarType) {
			sb.append(String.format("array[%s] of var %s: %s;\n", sCSet.toMzn(inst),
					encode(pvsType.getSpecType(), inst), valuationsArray));
		} else {
			MiniZincArrayLike arrayLike = (MiniZincArrayLike) specType;
			ArrayType arrayType = arrayLike.getArrayType();
			sb.append(String.format("array[%s,%s] of var %s: %s;\n", sCSet.toMzn(inst),
					getArrayIndexSets(arrayType, inst), encode(arrayType.getElementType(), inst), valuationsArray));
		}
		String topIdent = CodeGenerator.encodeString("top", inst);
		// String topElement = CodeGenerator.encodeString( pvsType.getTop(), inst);
		String topElement = CodeGenerator.processSubstitutions(pvsType.getTop(), subs);
		// TODO check if "top" can be used for something useful, got troublesome
		// due to array handling for now
		sb.append(String.format("par %s: %s = %s;\n", encode(pvsType.getElementType(), inst), topIdent, topElement));

		// -------------------------------------------------------------

		sb.append("\n% MiniSearch predicates: \n");
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

		inst.setGeneratedBetterPredicate(getBetterString);
		inst.setGeneratedNotWorsePredicate(notGetWorseString);

		// sb.append(String.format("function ann: %s() = post(%s(sol(%s), %s,
		// %s));\n",pvsPred, pvsType.getOrder(), overallIdent,
		// overallIdent,instanceArguments.toString()));

		sb.append(String.format("constraint %s = %s (%s,%s);\n", overallIdent, pvsType.getCombination(),
				valuationsArray, instanceArguments.toString()));

		sb.append("\n% Soft constraints: \n");
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

		// -------------------------------------------------------------
		if (genHeuristics) {
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

	private void appendOverall(StringBuilder sb, MiniZincParType elementType, PVSInstance inst, String overallIdent) {
		if (elementType instanceof MiniZincVarType) {
			sb.append(String.format("var %s: %s;\n", encode(elementType, inst), overallIdent));
		} else { // must be MiniZincArrayLike then
			String arrayDecisionVariable = getArrayDecisionVariable(elementType, inst, overallIdent);
			sb.append(arrayDecisionVariable + ";\n");
		}
	}

	private String getArrayDecisionVariable(MiniZincParType elementType, PVSInstance inst, String arrayIdent) {
		MiniZincArrayLike miniZincArrayLike = (MiniZincArrayLike) elementType;
		ArrayType arrayType = miniZincArrayLike.getArrayType();
		StringBuilder sb = new StringBuilder();

		sb.append("array[");
		sb.append(getArrayIndexSets(arrayType, inst));
		sb.append(String.format("] of var %s: %s", encode(arrayType.getElementType(), inst), arrayIdent));
		return sb.toString();
	}

	private String getArrayIndexSets(ArrayType arrayType, PVSInstance inst) {
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

	private String getOverallValuation(AbstractPVSInstance inst) {
		return encodeString(OVERALL_KEY, inst);
	}

	public AbstractPVSInstance deref(AbstractPVSInstance pvsInstance) {
		if (pvsInstance instanceof ReferencedPVSInstance) {
			ReferencedPVSInstance refPvs = (ReferencedPVSInstance) pvsInstance;
			return deref(refPvs.getReferencedInstance().instance);
		} else
			return pvsInstance;
	}

	public static String encodeString(String string, String instName) {
		return "mbr_" + string + "_" + instName;
	}

	public static String encodeString(String string, AbstractPVSInstance inst) {
		return encodeString(string, inst.getName());
	}

	private String encode(MiniZincParType type, PVSInstance concreteInstance) {
		return type.toMzn(concreteInstance);
	}

	public static String encodeIdent(PVSFormalParameter par, PVSInstance instance) {
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
}

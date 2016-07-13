package isse.mbr.parsing;

import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.parsetree.ProductType;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.parsetree.SoftConstraint;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincParType;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSParameter;
import isse.mbr.model.types.PVSType;

/**
 * Creates a MiniZinc file that contains all predicates, variables etc.
 * that are needed for minisearch
 * @author Alexander Schiendorfer
 *
 */
public class CodeGenerator {

	private final static Logger LOGGER = Logger.getGlobal();
	
	private static final String OVERALL_KEY = "overall";
	private static final String MBR_PREFIX = "mbr.";
	public static final String VALUATTIONS_PREFIX = "Valuations:";
	
	private List<String> leafValuations;
	
	public String generateCode(MiniBrassAST model) {
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
		for( Entry<String, PVSType> pvsType : model.getPvsTypes().entrySet()) {
			String referencedFile = pvsType.getValue().getImplementationFile();
			if(!addedFiles.contains(referencedFile)) {
				sb.append(String.format("include \"%s\";\n", referencedFile));
				addedFiles.add(referencedFile);
			}
		}
		
	}

	private void addPvsInstances(StringBuilder sb, MiniBrassAST model) {
		// start with solve item then continue only its referenced instances (that can play an active role)
		AbstractPVSInstance topLevelInstance = deref(model.getSolveInstance());
		
		sb.append("\n% ---------------------------------------------------");
		sb.append("\n% Overall exported predicate : \n");
		sb.append("\n% ---------------------------------------------------\n");
		
		String pvsPred = encodeString("postBetter", topLevelInstance);
		sb.append(String.format("function ann:  postGetBetter() = %s();\n",pvsPred));

		leafValuations = new LinkedList<>();
		addPvs(deref(topLevelInstance), sb, model);
		// add output line for valuation-carrying variables 
		sb.append("\n% Add this line to your output to make use of minisearch\n");
		sb.append("% [ \"\\n"+VALUATTIONS_PREFIX+" ");
		boolean first = true;
		for(String val : leafValuations) {
			if(!first)
				sb.append("; ");
			else 
				first = false;
			sb.append(String.format("%s = \\(%s)", val,val));
		}
		sb.append("\\n\"]\n");
	}

	private void addPvs(AbstractPVSInstance pvsInstance, StringBuilder sb, MiniBrassAST model) {
		if(pvsInstance instanceof CompositePVSInstance) {
			CompositePVSInstance comp = (CompositePVSInstance) pvsInstance;
			AbstractPVSInstance left = deref(comp.getLeftHandSide());
			AbstractPVSInstance right = deref(comp.getRightHandSide());
			
			addPvs(left, sb, model);
			addPvs(right, sb, model);
			
			if(comp.getProductType() == ProductType.DIRECT) {
				String pvsPred = encodeString("getBetter",  comp);
				String leftBetter = encodeString("getBetter",  left);
				String rightBetter = encodeString("getBetter",  right);
				sb.append(String.format("predicate %s() = (%s()) /\\ (%s());\n", pvsPred, leftBetter, rightBetter));
					
			} else { // lexicographic
				String pvsPred = encodeString("getBetter",  comp);
				String leftBetter = encodeString("getBetter",  left);
				String rightBetter = encodeString("getBetter",  right);
				
				// get left objective variable 
				String leftOverall = getOverallValuation(left);
				
				sb.append(String.format("predicate %s() = (%s() ) \\/ ( sol(%s) = %s /\\ %s() );\n", pvsPred, leftBetter, leftOverall, leftOverall, rightBetter));
			}
		} else {
			PVSInstance inst = (PVSInstance) pvsInstance;
			String name = inst.getName();
			/* 
			What we generate for a PVSType<E, S>(n, times o mu : S^n -> E, leq : E x E, top : E)

			array[1..n] of var S: softConstraintResults; % the applications of the individual soft constraints into the specification type
			var E: overall; 
			constraint overall = times(softConstraintResults); 
			constraint forall(i in 1..n) (is_worse(softConstraintResults[i], top) ); % maybe not even necessary, useful
			for minisearch: propagate "post( is_worse(sol(overall), overall) )" to BaB 
			for combined PVS (e.g. PVS_1 x PVS_2): post (is_worse(sol(overall_1), sol(overall_2), overall1, overall2 )
			generate string list of outputs of all overall values so minisearch can access them 
			*/
			sb.append("\n% ---------------------------------------------------");
			sb.append("\n%   PVS "+name);
			sb.append("\n% ---------------------------------------------------\n");
			
			// first the parameters 
			PVSType pvsType = inst.getType().instance;
			sb.append("% Parameters: \n");
			
			// prepare possible substitutions
			Map<String, String> subs = prepareSubstitutions(inst);
			
			for(Entry<String, PVSParamInst> entry : inst.getParametersLinked().entrySet()) {
				// first one should be nScs 
				PVSParamInst pi = entry.getValue();
				String def = String.format("%s : %s = %s; \n", encode(pi.parameter.getType(), inst),CodeGenerator.encodeIdent(pi.parameter, inst) , CodeGenerator.processSubstitutions(pi.expression, subs));
				sb.append(def);
			} 
			
			sb.append("\n% Decision variables: \n");
			
			String overallIdent = getOverallValuation(inst); 
			leafValuations.add(overallIdent);
			sb.append(String.format("var %s: %s;\n",encode(pvsType.getElementType(), inst), overallIdent));
			PVSParameter nScs = pvsType.getParamMap().get(PVSType.N_SCS_LIT);
			IntervalType sCSet = new IntervalType(new NumericValue(1), new NumericValue(nScs));
			
			String valuationsArray = CodeGenerator.encodeString("valuations", inst);
			
			
			sb.append(String.format("array[%s] of var %s: %s;\n", sCSet.toMzn(inst), encode(pvsType.getSpecType(), inst), valuationsArray));
			sb.append(String.format("constraint %s = %s (%s);\n", overallIdent, pvsType.getCombination() , valuationsArray));
		
			String topIdent = CodeGenerator.encodeString("top", inst);
			sb.append(String.format("par %s: %s = %s;\n", encode(pvsType.getElementType(), inst), topIdent, pvsType.getTop()));
			
			sb.append("\n% MiniSearch predicates: \n");
			// predicate getBetter() = x < sol(x) /\ y < sol(y);
			// predicate getBetter() = isWorse(sol(lb), violatedScs)
			String pvsPred = encodeString("postBetter", inst);
			
			StringBuilder instanceArguments = new StringBuilder();
			boolean first = true;
			for(PVSParameter pvsParam : pvsType.getPvsParameters()) {
				if(!first)
					instanceArguments.append(", ");
				else 
					first = false;
				String parIdent = CodeGenerator.encodeIdent(pvsParam, inst);
				instanceArguments.append(parIdent);
			}

			sb.append(String.format("function ann: %s() = post(%s(sol(%s), %s, %s));\n",pvsPred, pvsType.getOrder(), overallIdent, overallIdent,instanceArguments.toString()));
			
			sb.append("\n% Soft constraints: \n");
			for(SoftConstraint sc : inst.getSoftConstraints().values()) {
				sb.append(String.format("constraint %s[%d] = (%s);\n",valuationsArray,sc.getId(),sc.getMznLiteral()));
			}
		}
		
	}

	private Map<String, String> prepareSubstitutions(PVSInstance inst) {
		HashMap<String, String> substitutions = new HashMap<>();

		// all soft constraints map to their id
		for(SoftConstraint sc:  inst.getSoftConstraints().values()) {
			substitutions.put(MBR_PREFIX + sc.getName(), Integer.toString(sc.getId()));
		}
		
		// all parameters to their encoded id
		for(PVSParamInst pi : inst.getParametersLinked().values()) {
			String encodedIdent = CodeGenerator.encodeIdent(pi.parameter, inst);
			substitutions.put(MBR_PREFIX+pi.parameter.getName(), encodedIdent);
		}
		return substitutions;
	}

	private static String processSubstitutions(String expression, Map<String, String> subs) {
		for(Entry<String, String> entry : subs.entrySet()) {
			if(expression.contains(entry.getKey())){
				expression = expression.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return expression;
	}

	private String getOverallValuation(AbstractPVSInstance inst) {
		return encodeString(OVERALL_KEY, inst);
	}

	private AbstractPVSInstance deref(AbstractPVSInstance pvsInstance) {
		if (pvsInstance instanceof ReferencedPVSInstance) {
			ReferencedPVSInstance refPvs = (ReferencedPVSInstance) pvsInstance;
			return deref(refPvs.getReferencedInstance().instance);
		} else 
			return pvsInstance;
	}
	
	public static String encodeString(String string, String instName) {
		return "mbr_"+string+"_"+instName;
	}
	
	public static String encodeString(String string, AbstractPVSInstance inst) {
		return encodeString(string,inst.getName());
	}

	private String encode(MiniZincParType type, PVSInstance concreteInstance) {
		return type.toMzn(concreteInstance);
	}
	
	public static String encodeIdent(PVSParameter par, PVSInstance instance) {
		return encodeString(par.getName(), instance);
	}
}

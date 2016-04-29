package isse.mbr.parsing;

import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Set;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.CompositePVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
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
	
	public void generateCode(MiniBrassAST model) {
		// for now, just fill a string builder and print the console 
		System.out.println("\n\nSTARTING CODE GENERATION\n\n");
		StringBuilder sb = new StringBuilder("% ===============================================\n");
		sb.append("% Generated code from MiniBrass, do not modify!\n");

		addIncludes(sb, model);
		
		addPvsInstances(sb, model);
		
		// only final output here
		String fileContents = sb.toString();
		System.out.println();
		System.out.println(fileContents);
		
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
		AbstractPVSInstance topLevelInstance = model.getSolveInstance().instance;
		
		sb.append("\n% ---------------------------------------------------");
		sb.append("\n% Overall exported predicate : \n");
		sb.append("\n% ---------------------------------------------------\n");
		
		String pvsPred = encodeString("getBetter", topLevelInstance);
		sb.append(String.format("predicate getBetter() = %s();\n",pvsPred));

		addPvs(topLevelInstance, sb, model);
	}

	private void addPvs(AbstractPVSInstance pvsInstance, StringBuilder sb, MiniBrassAST model) {
		if(pvsInstance instanceof CompositePVSInstance) {
			CompositePVSInstance comp = (CompositePVSInstance) pvsInstance;
			addPvs(comp.getLeftHandSide(), sb, model);
			addPvs(comp.getRightHandSide(), sb, model);
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
			
			for(Entry<String, PVSParamInst> entry : inst.getParametersLinked().entrySet()) {
				// first one should be nScs 
				PVSParamInst pi = entry.getValue();
				String def = String.format("%s : %s = %s; \n", encode(pi.parameter.getType(), inst),CodeGenerator.encodeIdent(pi.parameter, inst) , pi.expression);
				sb.append(def);
			}
			
			sb.append("\n% Decision variables: \n");
			
			String overallIdent = encodeString("overall", inst);
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
			String pvsPred = encodeString("getBetter", inst);
			sb.append(String.format("predicate %s() = %s(sol(%s), %s);\n",pvsPred, pvsType.getOrder(), overallIdent, overallIdent));
		}
		
	}

	private static String encodeString(String string, AbstractPVSInstance inst) {
		return "mbr_"+string+"_"+inst.getName();
	}

	private String encode(MiniZincParType type, PVSInstance concreteInstance) {
		return type.toMzn(concreteInstance);
	}
	
	public static String encodeIdent(PVSParameter par, PVSInstance instance) {
		return encodeString(par.getName(), instance);
	}
}

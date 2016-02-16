package de.isse.parsing;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;


public class MinizincWCSPParser {
	private String problemName;
	private boolean debug = false;

	public StringBuilder parse(String fileName) {
		return parse(new File(fileName));
	}

	private StringBuilder parse(File file) {
		Scanner scanner = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			scanner = new Scanner(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		problemName = scanner.next().trim();
		sb.append("% Generated .mzn file: " + problemName + "\n");
		sb.append("% --------------------------------------- \n");
		sb.append("include \"table.mzn\";\n\n");
		
		if (debug)
			System.out.println("Problem name: " + problemName);
		int numberVariables = scanner.nextInt();
		if (debug)
			System.out.println("N Variables: " + numberVariables);
		int maxDomainSize = scanner.nextInt();
		if (debug)
			System.out.println("Max domain size: " + maxDomainSize);
		int numCostFunctions = scanner.nextInt();

		// now it's time to allocate some variables 
		sb.append("set of int: DOM  = 0.."+(maxDomainSize-1)+";\n");
		sb.append("set of int: VARS = 0.."+(numberVariables-1)+";\n");
		sb.append("int: nCfns = "+numCostFunctions+"; % number of cost function networks\n");
		sb.append("set of int: CFNS = 1..nCfns;\n");
		
		sb.append("array[VARS] of var DOM: x;\n");
		sb.append("array[CFNS] of var bool: v; % violated or not \n");
		sb.append("array[CFNS] of int: cost;\n");
		sb.append("int: sumCost;\n");
		sb.append("var 0..sumCost: OBJ;\n");
		sb.append("constraint OBJ = sum(i in CFNS) (bool2int(v[i])*cost[i] );\n");
		
		if (debug)
			System.out.println("Number of constraints: " + numCostFunctions);

		int upperBound = scanner.nextInt();
		sb.append("constraint OBJ <= "+upperBound+";\n\n");
		sb.append("% find the sorted permutation of soft constraint instances\n");
		sb.append("array[CFNS] of CFNS: sortPermScs = arg_sort(cost);\n");
		sb.append("% invert, since arg_sort use <= and we need decreasing order\n");
		sb.append("array[CFNS] of CFNS: mostImpFirst = [ sortPermScs[nCfns-s+1] | s in CFNS]; \n");		
		
		boolean convertibleToConstraintValuation = true;

		sb.append("\n");
		int[] domainSizes = new int[numberVariables];
		for (int i = 0; i < numberVariables; ++i) {
			int domainSize = scanner.nextInt();
			domainSizes[i] = domainSize;
			sb.append("constraint x["+i+"] < "+domainSize+";\n");
		}
		sb.append("\n");
		
		// now it's time for all cost functions
		if(debug)
			System.out.println("------- Cost functions");
		
		int constantCosts = 0;
		
		int sumVio = 0;
		int[] costViolations = new int[numCostFunctions];
		
		for (int i = 0; i < numCostFunctions; ++i) {

			// read the first line
			if (debug)
				System.out.println("Cost function " + (i + 1));

			int scopeSize = scanner.nextInt();
			StringBuilder originalLine = new StringBuilder(scopeSize + " ");
			
			if (debug)
				System.out.print("Talks about " + scopeSize
						+ " variables ... :\n  ");
			int[] scope = new int[scopeSize];

			StringBuilder mzVars = new StringBuilder("[");
			int nTuples = 1; // for the whole table
			boolean fVar = true;
			for (int variable = 0; variable < scopeSize; ++variable) {
				int nextVarIndex = scanner.nextInt();
				originalLine.append(nextVarIndex);
				originalLine.append(' ');
				
				if(fVar)
					fVar = false;
				else 
					mzVars.append(", ");
				mzVars.append("x["); mzVars.append(nextVarIndex);
				mzVars.append("]");
				scope[variable] = nextVarIndex;
				
				nTuples *= domainSizes[nextVarIndex];
				
				if (debug)
					System.out.print(nextVarIndex + " ");
			}
			mzVars.append("]");
			// mzVars.append("v["); mzVars.append(i+1); mzVars.append("]]");
			
			if (debug)
				System.out.println();
			HashSet<Integer> occurredCosts = new HashSet<Integer>();

			int defaultCost = scanner.nextInt();
			originalLine.append(defaultCost + " ");
			
			occurredCosts.add(defaultCost);

			int exceptions = scanner.nextInt();

			Set<List<Integer>> extensionSet = new HashSet<List<Integer>>(exceptions);

		    // necessary if you need the full extension!
		    // getExtensionMap(nTuples, scope, domainSizes, defaultCost); 
			if (debug)
				System.out.println("Def. Cost " + defaultCost + " but "
						+ exceptions + " exceptions");

			originalLine.append(exceptions);

			if(exceptions == 0) {
				// no exceptions, only add default cost
				if(defaultCost == 0) { // no effects, just continue
					continue;
				} else {
					constantCosts += defaultCost; // not sure what to do yet, let's raise an exception
					throw new RuntimeException("Incurred no exception tuples: "+constantCosts);
				}
			}
			
			int[][] extension = new int[exceptions][scopeSize];
			int[] costs = new int[exceptions];
			
			int deviatingCost = -1;
			
			for (int e = 0; e < exceptions; ++e) {
				if (debug)
					System.out.print("Exception " + (e + 1) + ": ");

				List<Integer> tupleAsList = new ArrayList<Integer>(scopeSize);
				
				for (int variable = 0; variable < scopeSize; ++variable) {
					int nextTupleValue = scanner.nextInt();
					extension[e][variable] = nextTupleValue;
					tupleAsList.add(nextTupleValue);
					if (debug)
						System.out.print(nextTupleValue + " ");
				}
				deviatingCost = scanner.nextInt();
				occurredCosts.add(deviatingCost);
				costs[e] = deviatingCost;
				
				extensionSet.add(tupleAsList);
			}
			
			StringBuilder extensionBuilder = new StringBuilder("[| ");
			boolean first = true;
			for(List<Integer> entry : extensionSet) {
				if(first)
					first = false;
				else 
					extensionBuilder.append(" | ");
				boolean f2 = true;
				for(Integer ii : entry ) {
					if(f2)
						f2 = false;
					else
						extensionBuilder.append(", ");
					extensionBuilder.append(ii);
				}
				extensionBuilder.append("\n");
			}
			extensionBuilder.append(" |]");
			
			if (debug)
				System.out.println();

			if (occurredCosts.size() == 2) {
				if(debug)
					System.out.println("BINARY valuation seen!");
			} else {
				if(debug)
					System.out.println("NON-binary valuation seen");

				if (occurredCosts.size() > 2 || ! occurredCosts.contains(0)) { // might be a single cost value!
					convertibleToConstraintValuation = false;
				} else {
					if (defaultCost == 0) {
						// all assignments yield no violation -> ignore cost
						// function
					} else {
						// I need to add a constant defaultCost
						if(debug)
							System.out.println("Constant overhead of "
								+ defaultCost);
						throw new RuntimeException("Well there is some constant cost?");
					}
				}
			}
			int costVio = Collections.max(occurredCosts);
			sumVio += costVio;
			costViolations[i] = costVio;
			
			String reification ="";
			if(defaultCost == 0) { // all extensions were violated
				reification = "<-> v["+(i+1)+"] ";
			} else { // all extensions were good
				reification = "xor v["+(i+1)+"] ";
			}
			
			sb.append("constraint table(" + mzVars.toString() +  ", "+extensionBuilder.toString() +")  "+reification+ ";\n");

		}
		
		StringBuilder costBuilder = new StringBuilder("[");
		for(int i = 0; i < costViolations.length; ++i) {
			if(i != 0)
				costBuilder.append(", ");
			costBuilder.append(costViolations[i]);
		}
		costBuilder.append("]");
		sb.append("sumCost = "+sumVio+";\n");
		sb.append("cost = "+costBuilder.toString()+";");
		
		if(debug)
			System.out.println("Is convertible? "
				+ convertibleToConstraintValuation);

		if (scanner != null)
			scanner.close();
		
		sb.append("\nsolve \n");
		sb.append(":: seq_search([  \n");
		sb.append("              int_search([ v[mostImpFirst[c]] | c in CFNS], input_order, indomain_min, complete), \n");
		sb.append("              int_search(x, dom_w_deg, indomain_min, complete) \n");
		sb.append("              ]) \n");
		sb.append("minimize OBJ; \n");
		return sb;
		// return cfn;
	}

	private Map<List<Integer>, Integer> getExtensionMap(int nTuples, int[] scope, int[] domainSizes, int defaultCost) {
		
		HashMap<List<Integer>, Integer> extensionMap = new HashMap<List<Integer>, Integer>(nTuples);
		ArrayList<Integer> values = new ArrayList<Integer>(scope.length);
		// just add scope.length 0 values so I can just use set
		for(int i = 0; i < scope.length; ++i)
			values.add(0);
		
		getExtensionMapRec(extensionMap, values, 0, scope, domainSizes, defaultCost);
		return extensionMap;
	}

	private void getExtensionMapRec(HashMap<List<Integer>, Integer> extensionMap, ArrayList<Integer> values, int i,
			int[] scope, int[] domainSizes, int defaultCost) {
		if(i >= scope.length)  { // time to add an assignment to the passed map
			ArrayList<Integer> tuple = new ArrayList<Integer>(values);
			extensionMap.put(tuple, defaultCost > 0 ? 1 : 0);
		} else {
			for(int d = 0; d < domainSizes[scope[i]]; ++d) {
				values.set(i, d);
				getExtensionMapRec(extensionMap, values, i+1, scope, domainSizes, defaultCost);
			}
		}
		
	}

	public static void main(String[] args) throws IOException {
		MinizincWCSPParser parser = new MinizincWCSPParser();
		String fileName = "8wqueens.wcsp";
		
		fileName = "minimal.wcsp";
		// fileName = "mprime03ac.wcsp";
		// fileName ="mprime03ac.wcsp";
		// fileName = "warehouse0.wcsp";
		// fileName = "16wqueens.wcsp";
		fileName = "slangford_3_11.wcsp";
		//fileName = "langford_2_4.wcsp";
		//fileName = "langford_3_11.wcsp";
		fileName = "genfrommin.wcsp";
		
		StringBuilder sb = parser
				.parse("problems/"+fileName);
		//System.out.println(sb.toString());
		String reduced = fileName.substring(0, fileName.indexOf("."));
		FileWriter fw = new FileWriter(new  File(reduced+".mzn"));
		fw.write(sb.toString());
		fw.close();
		System.out.println(sb.toString());
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}

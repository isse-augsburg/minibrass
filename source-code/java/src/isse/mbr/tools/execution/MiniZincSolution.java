package isse.mbr.tools.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Represents a particular MiniZinc solution
 * @author alexander
 *
 */
public class MiniZincSolution {

	private Map<String, MiniZincVariable> variableStore;
	
	// internal data structures
	private String rawDznSolution;
	private StringBuilder rawDznSolutionBuilder;
	
	public MiniZincSolution() {
		rawDznSolutionBuilder = new StringBuilder();
		variableStore = new HashMap<>();
	}
	
	/**
	 * A line of the current solution in the dzn format
	 * @param plainSolutionLine
	 */
	public void processSolutionLine(String plainSolutionLine) {
		rawDznSolutionBuilder.append(plainSolutionLine);
		StringTokenizer tokenizer = new StringTokenizer(plainSolutionLine, " ");
		String varIdent = tokenizer.nextToken();
		tokenizer.nextToken(); // equals sign
		String value = tokenizer.nextToken(";").trim();
		
		MiniZincVariable nextVariable = parseVariable(varIdent, value);
		variableStore.put(varIdent, nextVariable);
		System.out.println(varIdent + " -> "+value);
	}
	
	private MiniZincVariable parseVariable(String varIdent, String value) {
		if(value.contains("{")) { // will be a set
			
		} else if (value.contains("[")) { // will be an array
			
		} else { // should be int
			
		}
		return null;
	}

	public void flush() {
		rawDznSolution = rawDznSolutionBuilder.toString();
	}
	
	public String getRawDznSolution() {
		return rawDznSolution;
	}
}

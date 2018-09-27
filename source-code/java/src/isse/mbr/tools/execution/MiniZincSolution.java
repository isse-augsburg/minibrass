package isse.mbr.tools.execution;

import java.util.HashMap;
import java.util.Map;

import isse.mbr.parsing.DznParser;
import isse.mbr.parsing.MiniBrassParseException;

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
	private DznParser dznParser;
	
	public MiniZincSolution() {
		rawDznSolutionBuilder = new StringBuilder();
		variableStore = new HashMap<>();
		dznParser = new DznParser();
	}
	
	/**
	 * A line of the current solution in the dzn format
	 * @param plainSolutionLine
	 */
	public void processSolutionLine(String plainSolutionLine) {
		System.out.println("Processing: " + plainSolutionLine);
		rawDznSolutionBuilder.append(plainSolutionLine);
		MiniZincVariable nextVariable = null; 
		try {
			nextVariable = dznParser.parseVariable(plainSolutionLine.trim());
		} catch (MiniBrassParseException e) {
			System.out.println("Error here");
			e.printStackTrace();
		}
		variableStore.put(nextVariable.getName(), nextVariable);
		System.out.println(nextVariable.getName() + " -> "+ nextVariable.getValue());
	}

	public void flush() {
		rawDznSolution = rawDznSolutionBuilder.toString();
	}
	
	public String getRawDznSolution() {
		return rawDznSolution;
	}
}

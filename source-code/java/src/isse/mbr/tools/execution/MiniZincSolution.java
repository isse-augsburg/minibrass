package isse.mbr.tools.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import isse.mbr.model.types.StringType;
import isse.mbr.parsing.DznParser;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Represents a particular MiniZinc solution
 * @author alexander
 *
 */
public class MiniZincSolution {

	private static final String OUTPUT_ITEM_VARIABLE_NAME = "_output";
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
		rawDznSolutionBuilder.append(plainSolutionLine);
		rawDznSolutionBuilder.append("\n");
		MiniZincVariable nextVariable = null;
		try {
			nextVariable = dznParser.parseVariable(plainSolutionLine.trim());
			variableStore.put(nextVariable.getName(), nextVariable);
		} catch (MiniBrassParseException e) {
			// ignore: output line has wrong format and thus is not of interest for us
		}
	}

	public void flush() {
		rawDznSolution = rawDznSolutionBuilder.toString();
	}

	public String getRawDznSolution() {
		return rawDznSolution;
	}

	public List<MiniZincVariable> getAllVariables() {
		return new ArrayList<MiniZincVariable>(variableStore.values());
	}

	public MiniZincVariable getVariable(String key) {
		return variableStore.get(key);
	}

	public boolean hasOutputItem() {
		return variableStore.containsKey(OUTPUT_ITEM_VARIABLE_NAME);
	}

	public String getOutputItem() {
		var variable = getVariable(OUTPUT_ITEM_VARIABLE_NAME);
		if (variable == null || !(variable.getType() instanceof StringType)) return null;
		String outputItem = variable.getValue().toString();
		return StringEscapeUtils.unescapeJava(outputItem);
	}
}

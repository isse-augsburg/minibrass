package isse.mbr.tools.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import isse.mbr.parsing.DznParser;
import isse.mbr.parsing.MiniBrassParseException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
		rawDznSolutionBuilder.append(plainSolutionLine);
		rawDznSolutionBuilder.append("\n");
		MiniZincVariable nextVariable = null;
		try {
			nextVariable = dznParser.parseVariable(plainSolutionLine.trim());
		} catch (MiniBrassParseException e) {
			e.printStackTrace();
		}
		variableStore.put(nextVariable.getName(), nextVariable);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		MiniZincSolution solution = (MiniZincSolution) o;

		return new EqualsBuilder().append(variableStore, solution.variableStore).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(variableStore).toHashCode();
	}

	@Override
	public String toString() {
		return rawDznSolution;
	}
}

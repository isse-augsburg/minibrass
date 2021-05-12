package isse.mbr.tools.execution;

import java.util.LinkedList;
import java.util.List;

public class MiniZincResult {
	// properties
	private boolean solved;
	private boolean optimal;
	private boolean invalidated;
	private boolean unsatisfiable;
	private String formattedOutput;
	private List<MiniZincSolution> solutions;

	// internal data structures
	private MiniZincSolution nextSolution;
	private List<String> dznLines;
	private StringBuilder completeOutputBuilder;

	// constants
	private final static String OPTIMALITY_SEP = "==========";
	private final static String SOLUTION_SEP = "----------";
	private final static String UNSATISFIABLE_SEP = "=====UNSATISFIABLE=====";
	private final static String UNKNOWN_SEP = "=====UNKNOWN====="; // in case of timeout


	public MiniZincResult() {
		solved = false;
		optimal = false;
		unsatisfiable = false;
		solutions = new LinkedList<>();
		formattedOutput = null;
		invalidated = false;
		completeOutputBuilder = new StringBuilder();
		prepareForNextSolution();
	}

	public void processRawMiniZincOutputLines(List<String> lines) {
		for(String line : lines)
			processRawMiniZincOutputLine(line);
	}

	/**
	 * This is for a first pass over the dzn output - if we find model inconsistencies already
	 * @param line
	 */
	public void lookForError(String line) {
		if(line.contains(UNSATISFIABLE_SEP) || line.contains(UNKNOWN_SEP)) {
			solved = false;
			invalidate();
			unsatisfiable = true;
		}


		if (line.toLowerCase().contains("error") ) {
			invalidate();
		}
	}

	/**
	 * This method takes a raw line from the MiniZinc output and
	 * @param line
	 */
	public void processRawMiniZincOutputLine(String line) {
		completeOutputBuilder.append(line);
		completeOutputBuilder.append("\n");

		boolean plainSolutionLine = true;

		if (line.contains(OPTIMALITY_SEP)) {
			solved = true;
			optimal = true;
			plainSolutionLine = false;
		}

		if (line.contains(SOLUTION_SEP)) {
			solved = true;
			plainSolutionLine = false;
			prepareForNextSolution();
		}


		if(plainSolutionLine && !invalidated) {
			nextSolution.processSolutionLine(line);
		}
	}

	/**
	 * A result may be invalidated in case of an exception having occurred
	 */
	public void invalidate() {
		invalidated = true;
	}

	private void prepareForNextSolution() {
		if(nextSolution != null) {
			nextSolution.flush();
			solutions.add(nextSolution);
		}
		nextSolution = new MiniZincSolution();
		dznLines = new LinkedList<>();
	}
	//================== Setters and getters start =====================
	public MiniZincSolution getLastSolution() {
		if(solved) {
			return solutions.get(solutions.size()-1);
		}
		return null;
	}

	public boolean isInvalidated() {
		return invalidated;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public boolean isSolvedAndValid() {
		return !isInvalidated() && isSolved();
	}

	public boolean isOptimal() {
		return optimal;
	}

	public void setOptimal(boolean optimal) {
		this.optimal = optimal;
	}

	public String getFormattedOutput() {
		return formattedOutput;
	}

	public void setFormattedOutput(String formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	public List<MiniZincSolution> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<MiniZincSolution> solutions) {
		this.solutions = solutions;
	}

	public boolean isUnsatisfiable() {
		return unsatisfiable;
	}

	public void setUnsatisfiable(boolean unsatisfiable) {
		this.unsatisfiable = unsatisfiable;
	}

}

package de.isse.results;

import java.io.Serializable;

import de.isse.jobs.JobResult;

public class MiniBrassResult implements Serializable {

	private static final long serialVersionUID = -5832027890688834180L;

	public double elapsedSeconds;

	public boolean solvedOptimally;

	public boolean solved;

	public int objective;

	public int noSolutions; // how many solutions did we get to see during optimization 
	
	public boolean valid; // only written if run was properly completed

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof JobResult))
			return false;
		else {
			MiniBrassResult other = (MiniBrassResult) obj;
			try {
				return elapsedSeconds == other.elapsedSeconds && solvedOptimally == other.solvedOptimally && solved == other.solved && objective == other.objective && noSolutions == other.noSolutions && valid == other.valid;
			} catch (NullPointerException ne) {
				throw new RuntimeException("There should be no unhandled null values! ", ne);
			}
		}
	}

	@Override
	public int hashCode() {
		return new Double(elapsedSeconds).hashCode() + new Boolean(solved).hashCode()
				+ new Boolean(solvedOptimally).hashCode() + new Integer(objective).hashCode() + new Integer(noSolutions).hashCode() + new Boolean(valid).hashCode();
	}

	public MiniBrassResult() {
		this.objective = Integer.MAX_VALUE;
		this.valid = false;
	}
	
	@Override
	public String toString() {
		if(!valid)
			return "Invalid result";
		StringBuilder sb = new StringBuilder("Solved: ");
		sb.append(solved); sb.append(", ");
		sb.append("Optimally: "); sb.append(solvedOptimally); sb.append(", ");
		sb.append("Objective: "); sb.append(objective); sb.append(", ");
		sb.append("NoSolutions: "); sb.append(noSolutions); sb.append(", ");
		sb.append("Seconds: "); sb.append(elapsedSeconds); sb.append(", ");
		
		return sb.toString();
	}
}

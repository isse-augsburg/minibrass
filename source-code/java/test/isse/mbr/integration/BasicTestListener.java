package isse.mbr.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import isse.mbr.parsing.CodeGenerator;

/**
 * This listener reads the last found solution from test problems that adhere to an output standard
 * @author Alexander Schiendorfer
 *
 */
public class BasicTestListener implements MiniZincResultListener {

	private boolean solved;
	private boolean optimally;
	private String customString;
	private boolean cyclic;
	private Map<String, String> objectives;
	private Map<String, String> lastSolution;
	
	public BasicTestListener() {
		this.solved = false;
		this.optimally = false;
		this.cyclic = false;
		this.objectives = new HashMap<>();
		this.lastSolution = new HashMap<>();
	}
	
	@Override
	public void notifyOptimality() {
		this.optimally = true;
	}

	@Override
	public void notifyLine(String line) {
		boolean solutionLine = line.startsWith("Intermediate solution:" );
		boolean objLine = line.startsWith(CodeGenerator.VALUATTIONS_PREFIX);
		boolean customLine = line.startsWith("Custom:");
		
		cyclic = cyclic || line.contains("cycl");
		
		if(solutionLine || objLine) {
			String valuations = line.substring(line.lastIndexOf(':')+1, line.length());

			StringTokenizer tokenizer = new StringTokenizer(valuations, ";");
			while(tokenizer.hasMoreTokens()) {
				String tok = tokenizer.nextToken().trim();
				
				Scanner sc = new Scanner(tok);
				String id = sc.next();
				sc.next(); // "="
				String val = sc.next();
				
				if(solutionLine)
					lastSolution.put(id, val);
				else
					objectives.put(id, val);
				
				sc.close();
			}
		}
		
		if(customLine) {
			customString = line.substring(line.lastIndexOf(':')+1, line.length());
		}
	}

	@Override
	public void notifySolved() {
		this.solved = true;
	}

	public boolean isSolved() {
		return solved;
	}

	public boolean isOptimal() {
		return optimally;
	}

	public Map<String,String> getObjectives() {
		return objectives;
	}

	public Map<String, String> getLastSolution() {
		return lastSolution;
	}

	public String getCustomString() {
		return customString;
	}

	public boolean isCyclic() {
		return cyclic;
	}
}

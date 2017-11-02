package isse.mbr.tools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	private int solutionCounter;
	private boolean optimally;
	private String customString;
	private boolean cyclic;
	private Map<String, String> objectives;
	private Map<String, List<String>> objectiveSequences;
	private Map<String, String> lastSolution;
	private boolean wasSolutionLine;
	private boolean wasObjLine;
	
	public BasicTestListener() {
		this.solved = false;
		this.solutionCounter = 0;
		this.optimally = false;
		this.cyclic = false;
		this.objectives = new HashMap<>();
		this.lastSolution = new HashMap<>();
		this.objectiveSequences = new HashMap<>(); 
	}
	
	
	@Override
	public void notifyOptimality() {
		this.optimally = true;
	}

	@Override
	public void notifyLine(String line) {
		wasSolutionLine = line.startsWith(CodeGenerator.INTERMEDIATE_SOLUTIONS_PREFIX);
		wasObjLine = line.startsWith(CodeGenerator.VALUATTIONS_PREFIX);
		boolean customLine = line.startsWith("Custom:");
		
		cyclic = cyclic || line.contains("cycl");
		
		if(wasSolutionLine || wasObjLine) {
			String valuations = line.substring(line.lastIndexOf(':')+1, line.length());

			StringTokenizer tokenizer = new StringTokenizer(valuations, ";");
			while(tokenizer.hasMoreTokens()) {
				String tok = tokenizer.nextToken().trim();
				
				Scanner sc = new Scanner(tok);
				String id = sc.next();
				sc.next(); // "="
				String val = sc.nextLine().trim();
				
				if(wasSolutionLine)
					lastSolution.put(id, val);
				else {
					
					objectives.put(id, val);
					if(objectiveSequences.containsKey(id)) {
						objectiveSequences.get(id).add(val);
					} else {
						List<String> values = new LinkedList<>();
						values.add(val);
						objectiveSequences.put(id, values);								
					}
				}
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
		++this.solutionCounter;
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

	public int getSolutionCounter() {
		return solutionCounter;
	}

	public void setSolutionCounter(int solutionCounter) {
		this.solutionCounter = solutionCounter;
	}


	public Map<String, List<String>> getObjectiveSequences() {
		return objectiveSequences;
	}


	public void setObjectiveSequences(Map<String, List<String>> objectiveSequences) {
		this.objectiveSequences = objectiveSequences;
	}


	public boolean isWasSolutionLine() {
		return wasSolutionLine;
	}


	public boolean wasObjLine() {
		return wasObjLine;
	}

}

package isse.mbr.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import isse.mbr.parsing.MiniBrassCompiler;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.parsing.MiniBrassParser;

/**
 * The solution recorder takes a constraint satisfaction model along with a MiniBrass
 * preference specification and records all solution degrees it finds to a .csv file 
 * @author Alexander Schiendorfer
 *
 */
public class SolutionRecorder {
	public class Solution {
		protected int solutionId;
		public Map<String, String> objectives;
		public Map<String, String> assignment;
		
		public Solution(int solutionId) {
			this.solutionId = solutionId;
			objectives = new HashMap<>();
			assignment = new HashMap<>();
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("Solution #");
			sb.append(solutionId);
			sb.append(": [");
			boolean first = true;
			for( Entry<String, String> assignmentEntry : assignment.entrySet()) {
				if (first)
					first = false;
				else 
					sb.append(", ");
				
				sb.append(assignmentEntry.getKey());
				sb.append(" -> ");
				sb.append(assignmentEntry.getValue());
			}
			sb.append("]");
			return sb.toString();
		}
	}
	
	public class SolutionRecorderListener implements MiniZincResultListener {
		protected BasicTestListener decoratedListener;
		protected int solutionCount;
		
		public SolutionRecorderListener() {
			decoratedListener = new BasicTestListener();
			recordedSolutions = new LinkedList<>();
			solutionCount = 0; 
		}
		
		@Override
		public void notifyOptimality() {
			decoratedListener.notifyOptimality();
		}

		@Override
		public void notifyLine(String line) {
			// this is a generated output that we should be easily able to parse 
			decoratedListener.notifyLine(line);
			if(decoratedListener.wasObjLine()) {
				Solution s = new Solution(++solutionCount);
				
				s.objectives.putAll(decoratedListener.getObjectives());
				s.assignment.putAll(decoratedListener.getLastSolution());
				
				recordedSolutions.add(s);
				}
		}

		@Override
		public void notifySolved() {
			decoratedListener.notifySolved();
			modelSolved = true;
		}

	}

	protected MiniZincLauncher launcher;
	protected List<Solution> recordedSolutions;
	protected boolean modelSolved;
	protected MiniBrassParser parser;
	
	
	public SolutionRecorder() {
		this.launcher = new MiniZincLauncher();
	}
	
	/**
	 * Start the recording process 
	 * @param miniZincFile a *satisfaction* model (solve satisfy) 
	 * @param miniBrassFile my MiniBrass file of choice 
	 * @param dataFiles
	 * @throws MiniBrassParseException 
	 * @throws IOException 
	 */
	public void recordSolutions(File miniZincFile, File miniBrassFile, Collection<File> dataFiles) throws MiniBrassParseException, IOException {
		MiniBrassCompiler compiler = new MiniBrassCompiler();
		compiler.setGenHeuristics(false);
		compiler.setMinizincOnly(true);
		compiler.compile(miniBrassFile);
		
		parser = compiler.getUnderlyingParser();
		
		File dataFile = null;
		// TODO for now just one data file 
		for(File f:dataFiles) {
			dataFile = f;
			break;
		}
		launcher.addMiniZincResultListener(new SolutionRecorderListener());		
		launcher.runMiniZincModel(miniZincFile, dataFile, 100);
	}
	
	public void recordSolutions(File miniZincFile, File miniBrassFile) throws MiniBrassParseException, IOException {
		recordSolutions(miniZincFile, miniBrassFile, new ArrayList<File>());
	}
	
	public static void main(String[] args) throws MiniBrassParseException, IOException {
		SolutionRecorder sr = new SolutionRecorder();
		File miniZincFile = new File("test-models/solRecord.mzn");
		File miniBrassFile = new File("test-models/solRecord.mbr");
		
		sr.recordSolutions(miniZincFile, miniBrassFile);
		
		PairwiseComparator pc = new PairwiseComparator();
		pc.performPairwiseComparison(sr);
		System.out.println(pc.getHtml());
	}

	public List<Solution> getRecordedSolutions() {
		return recordedSolutions;
	}

	public MiniBrassParser getParser() {
		return parser;
	}

	public void setParser(MiniBrassParser parser) {
		this.parser = parser;
	}
}

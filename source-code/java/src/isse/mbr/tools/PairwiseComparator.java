package isse.mbr.tools;

import java.util.List;

import isse.mbr.tools.SolutionRecorder.Solution;

/**
 * Performs actual pairwise comparison of solutions 
 * 
 * @author Alexander Schiendorfer
 *
 */
public class PairwiseComparator {
	public void performPairwiseComparison(SolutionRecorder sr) {
		List<Solution> solutions = sr.getRecordedSolutions();
		for(int i = 0; i < solutions.size(); ++i) {
			Solution left = solutions.get(i);
			
			for(int j = i+1; j < solutions.size(); ++j) {
				// comparing solution i and j 
				Solution right = solutions.get(j);
				
				System.out.println("Comparing solutions .... ");
				System.out.println("   " + left + " with ");
				System.out.println("   " + right);
			}
		}
	}
}

package isse.mbr.extensions.weighting;

import isse.mbr.extensions.ExternalMorphism;
import isse.mbr.extensions.domain.DirectedGraph;
import isse.mbr.extensions.domain.UtilityStructure;
import isse.mbr.model.parsetree.PVSInstance;

/**
 * This hook is intended to take a MiniZinc-encoded graph,
 * perform multiweighting and export a MiniZinc-2d array with weights
 * @author Alexander Schiendorfer
 *
 */
public class MultiWeighting extends ExternalMorphism {
	@Override
	public void process(PVSInstance fromInstance) {
		String generatedCrEdges = fromInstance.getGeneratedCodeParameters().get("crEdges");
		int nScs = fromInstance.getNumberSoftConstraints();
		processMiniZincString(generatedCrEdges, nScs);
	}

	public DirectedGraph processMiniZincString(String generatedCrEdges, int nScs) {

		// TODO for now, we expect a literal graph in the form "[| 2, 1 | 1, 0 |]"
		// assumes strictly that soft constraints are labeled from 1 to nScs
	
		DirectedGraph dag = new DirectedGraph(nScs);
		dag.parse(generatedCrEdges);

		MultipleUtilities mu = new MultipleUtilities();
		UtilityStructure us = mu.findUtilities(dag);
		
		StringBuilder mzGraphBuilder = new StringBuilder("[|");
		for(int i = 0; i < us.getWeights().length; ++i) {
			if(i > 0)
				mzGraphBuilder.append(" | ");
			for(int j = 0; j < us.getSize(); ++j) {
				if(j > 0)
					mzGraphBuilder.append(", ");
				mzGraphBuilder.append(us.getWeights()[i][j]);
			}
			
		}
		mzGraphBuilder.append("|]");
		// 
		calculatedParameters.put("weights", mzGraphBuilder.toString());
		calculatedParameters.put("d", Integer.toString(us.getSize()));
		return dag;
	}

}

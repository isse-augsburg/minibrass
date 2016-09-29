package isse.mbr.extensions.weighting;

import java.util.StringTokenizer;

import isse.mbr.extensions.ExternalMorphism;
import isse.mbr.extensions.domain.DirectedGraph;
import isse.mbr.extensions.domain.UtilityStructure;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.types.PVSParamInst;

/**
 * This hook is intended to take a MiniZinc-encoded graph,
 * perform multiweighting and export a MiniZinc-2d array with weights
 * @author Alexander Schiendorfer
 *
 */
public class MultiWeighting extends ExternalMorphism {
	@Override
	public void process(PVSInstance fromInstance) {
		PVSParamInst pi = fromInstance.getParametersInstantiated().get("crEdges");
		String generatedCrEdges = fromInstance.getGeneratedCodeParameters().get("crEdges");
		
		// TODO for now, we expect a literal graph in the form "[| 2, 1 | 1, 0 |]"
		// assumes strictly that soft constraints are labeled from 1 to nScs
		String processed = generatedCrEdges.replaceAll("\\[\\|", "");
		processed = processed.replaceAll("\\|\\]", "").trim();
		StringTokenizer tok = new StringTokenizer(processed, "|");
		
		int nScs = fromInstance.getNumberSoftConstraints();
		DirectedGraph dag = new DirectedGraph(nScs);
		
		while(tok.hasMoreTokens()) {
			String nextEdge = tok.nextToken().trim();
			String[] splitted = nextEdge.split(",");
			int fromId = Integer.parseInt(splitted[0].trim())-1;
			int toId = Integer.parseInt(splitted[1].trim())-1;
			dag.markEdge(fromId, toId);
		} 
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
	}

}

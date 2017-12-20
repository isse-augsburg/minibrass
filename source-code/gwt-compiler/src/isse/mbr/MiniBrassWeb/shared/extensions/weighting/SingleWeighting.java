package isse.mbr.MiniBrassWeb.shared.extensions.weighting;

import java.util.Map;

import isse.mbr.MiniBrassWeb.shared.extensions.ExternalMorphism;
import isse.mbr.MiniBrassWeb.shared.extensions.domain.DirectedGraph;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.PVSInstance;

/**
 * This hook is intended to take a MiniZinc-encoded graph,
 * perform single weighting and export a MiniZinc-1d array with weights
 * @author Alexander Schiendorfer
 *
 */
public class SingleWeighting extends ExternalMorphism {
	@Override
	public void process(PVSInstance fromInstance) {
		String generatedCrEdges = fromInstance.getGeneratedCodeParameters().get("crEdges");
		int nScs = fromInstance.getNumberSoftConstraints();
		processMiniZincString(generatedCrEdges, nScs);
	}

	public DirectedGraph processMiniZincString(String generatedCrEdges, int nScs) {

		// TODO for now, we expect a literal graph in the form "[| 2, 1 | 1, 0 |]"
		// assumes strictly that soft constraints are labeled from 1 to nScs
		String processed = generatedCrEdges.replaceAll("\\[\\|", "");
		processed = processed.replaceAll("\\|\\]", "").trim();
		processed = processed.replaceAll("transClosureWrap", "");
		processed = processed.replaceAll("\\(", "");
		processed = processed.replaceAll("\\)", "");
		
		String[] tok = processed.split("\\|");
		
		DirectedGraph dag = new DirectedGraph(nScs);
		
		for(String nextEdge: tok) {
			nextEdge = nextEdge.trim();
			String[] splitted = nextEdge.split(",");
			int fromId = Integer.parseInt(splitted[0].trim())-1;
			int toId = Integer.parseInt(splitted[1].trim())-1;
			dag.markEdge(fromId, toId);
		} 
		SingleUtility su = new SingleUtility();
		Map<Integer, Integer> retVal = su.findUtilities(dag);
		
		
		StringBuilder mzGraphBuilder = new StringBuilder("[");
		int k = 0;
		for(int j = 0; j < retVal.keySet().size(); ++j) {
			if(j > 0)
				mzGraphBuilder.append(", ");
			mzGraphBuilder.append(retVal.get(j));
			k = Math.max(k, retVal.get(j));
		}
		k = k * nScs + 1;
		mzGraphBuilder.append("]");
		// 
		calculatedParameters.put("weights", mzGraphBuilder.toString());
		calculatedParameters.put("k", Integer.toString(k));
		return dag;
	}

}

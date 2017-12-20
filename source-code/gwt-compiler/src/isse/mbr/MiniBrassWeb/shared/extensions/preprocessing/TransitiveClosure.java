package isse.mbr.MiniBrassWeb.shared.extensions.preprocessing;

import isse.mbr.MiniBrassWeb.shared.extensions.ExternalParameterWrap;
import isse.mbr.MiniBrassWeb.shared.extensions.domain.DirectedGraph;
import isse.mbr.MiniBrassWeb.shared.graph.DEdge;
import isse.mbr.MiniBrassWeb.shared.graph.DGraph;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.PVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSFormalParameter;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

public class TransitiveClosure extends ExternalParameterWrap {

	@Override
	public String process(PVSInstance pvsInst, PVSFormalParameter pvsParam, String parameter)
			throws MiniBrassParseException {

		DirectedGraph digraph = new DirectedGraph(pvsInst.getNumberSoftConstraints());
		digraph.parse(parameter);

		DGraph<Integer> jgraph = new DGraph<>();

		// transcribe
		digraph.transcribe(jgraph);
		jgraph.closeTransitive();
		StringBuilder mznOutput = new StringBuilder("[|");
		boolean first = true;
		for (DEdge<Integer> de : jgraph.edgeSet()) {
			int from = jgraph.getEdgeSource(de);
			int to = jgraph.getEdgeTarget(de);
			if (first)
				first = false;
			else
				mznOutput.append(" | ");
			mznOutput.append(from);
			mznOutput.append(", ");
			mznOutput.append(to);

		}
		mznOutput.append("|]");
		return mznOutput.toString();
	}

}

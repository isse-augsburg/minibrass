package isse.mbr.extensions.preprocessing;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import isse.mbr.extensions.ExternalParameterWrap;
import isse.mbr.extensions.domain.DirectedGraph;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.types.PVSFormalParameter;
import isse.mbr.parsing.MiniBrassParseException;

public class TransitiveClosure extends ExternalParameterWrap {

	@Override
	public String process(PVSInstance pvsInst, PVSFormalParameter pvsParam, String parameter) throws MiniBrassParseException {
	
		DirectedGraph digraph = new DirectedGraph(pvsInst.getNumberSoftConstraints());
		digraph.parse(parameter);
		
		DirectedAcyclicGraph<Integer, DefaultEdge> jgraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
			
		// transcribe 
		digraph.transcribe(jgraph);
		org.jgrapht.alg.TransitiveClosure tc = org.jgrapht.alg.TransitiveClosure.INSTANCE;
		tc.closeSimpleDirectedGraph(jgraph);
		StringBuilder mznOutput = new StringBuilder("[|");
		boolean first = true;
		for(DefaultEdge de : jgraph.edgeSet()) {
			int from = jgraph.getEdgeSource(de);
			int to = jgraph.getEdgeTarget(de);
			if(first)
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

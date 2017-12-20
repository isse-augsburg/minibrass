package isse.mbr.MiniBrassWeb.shared.extensions.weighting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import isse.mbr.MiniBrassWeb.shared.extensions.domain.DirectedGraph;
import isse.mbr.MiniBrassWeb.shared.extensions.domain.UtilityStructure;

public class MultipleUtilities {
	private static class WeightSource {
		public int from; 
		public int utilityIndex;
		
		public WeightSource(int from, int utilityIndex) {
			super();
			this.from = from;
			this.utilityIndex = utilityIndex;
		}
	}
	
	public UtilityStructure findUtilities(DirectedGraph digraph) {
		
		boolean[][] adjacencyCopy =  new boolean[digraph.getAdjacencyMatrix().length][];
		int edgeCount = 0; 
		for(int i = 0; i < digraph.getAdjacencyMatrix().length; i++) {
			
		    boolean[] adjacency = digraph.getAdjacencyMatrix()[i];
			adjacencyCopy[i] =  Arrays.copyOf(adjacency, adjacency.length);
		    
		    for(int j = 0; j < digraph.size(); ++j) {
		    	if(adjacency[j])
		    		++edgeCount;
		    }
		}
		// number of utility functions is probably bounded by Max(|E|, |V|), not |V| or |E| alone 
		edgeCount = Math.max(digraph.size(), edgeCount);
		
		int[][] weights = new int[digraph.size()][edgeCount];
		boolean[] taken = new boolean[digraph.size()];
		
		// useful help data structures 
		List<List<WeightSource>> pool = new ArrayList<List<WeightSource>>(digraph.size());
		List<List<Integer>> out = new ArrayList<List<Integer>>(digraph.size());
		List<List<WeightSource>> inc = new ArrayList<List<WeightSource>>(digraph.size());
		
		int[] maxWeights = new int[digraph.size()];
		
		for(int i = 0; i < digraph.size(); ++i) {
			pool.add(new LinkedList<WeightSource>());
			out.add(new LinkedList<Integer>());
			inc.add(new LinkedList<WeightSource>());
		}
		// now each i has their own set of lists (in, out, inc)
		
		final int initValue = 1;
		
		// do some kind of top sort here 
		int nodesLeft = digraph.size(); 
		
		int nextUtility = 0; // index of the next utility function 
		
		while(nodesLeft > 0) {
			// now just pick the next node without an incoming edge 
			int nextNode = -1;
			for(int i = 0; i < digraph.size(); ++i){
				if(taken[i])
					continue;
				
				boolean hasIncoming = false;
				for(int j = 0; j < digraph.size(); ++j) {
					if(adjacencyCopy[j][i]) {
						hasIncoming = true;
						break;
					}
				}
				
				if(hasIncoming)
					continue;
				else {
					nextNode = i;
					for(int j = 0; j < digraph.size(); ++j) {
						adjacencyCopy[nextNode][j] = false;
					}
					break;
				}
			}
			
			if (nextNode == -1) { // we cycled through all nodes without finding one with indegree 0
				throw new RuntimeException("Cyclic graph submitted for weighting!");
			}
			// assert nextNode >= 0 && nextNode <= digraph.size()
			
			// if this is a node without incoming increase requests -> spawn a new utility
			if(inc.get(nextNode).isEmpty()) {
				int newUtil = nextUtility++;
				weights[nextNode][newUtil] = initValue;
				maxWeights[newUtil] = initValue;
				out.get(nextNode).add(newUtil);
			}
			
			// first, copy all values for the cost functions in pool[nextNode]
			for(WeightSource ws : pool.get(nextNode)) {
				weights[nextNode][ws.utilityIndex] = weights[ws.from][ws.utilityIndex]; 
				out.get(nextNode).add(ws.utilityIndex);
			}
			
			// second, increment all that were in "inc"
			for(WeightSource ws : inc.get(nextNode)) {
				int newWeight = updateRule(weights[ws.from][ws.utilityIndex]);
				if(weights[nextNode][ws.utilityIndex] < newWeight) {
					weights[nextNode][ws.utilityIndex] = newWeight;
					maxWeights[ws.utilityIndex] = newWeight;
				}
				out.get(nextNode).add(ws.utilityIndex);
			}
			
			// work out what we need to send to out (we might spawn new utilities, actually)
			int k = 0; 
			List<WeightSource> outPool = new ArrayList<WeightSource>(out.size());
			
			// not really necessary, but nice for testing 
			Collections.sort(out.get(nextNode));
			for (int j = 0; j < digraph.size(); ++j) {
				if(digraph.getAdjacencyMatrix()[nextNode][j]) {
					// we need to add input to node j 
					WeightSource newWs = null;
					
					if(k < out.get(nextNode).size()) {
						// pick utility k from in
						int ws = out.get(nextNode).get(k);
						newWs = new WeightSource(nextNode, ws);
						++k;
						
					} else {
						// spawn a new utility 
						int newUtil = nextUtility++;

						weights[nextNode][newUtil] = initValue;
						maxWeights[newUtil] = initValue;
						newWs = new WeightSource(nextNode, newUtil);												
					}
					inc.get(j).add(newWs);
				}
			}
			
			// if there is some of our input left
			for(; k < out.get(nextNode).size(); ++k) {
				outPool.add(new WeightSource(nextNode, out.get(nextNode).get(k)));
			}
			
			// tell *all* outgoing edges about our pool so they can copy 
			for (int j = 0; j < digraph.size(); ++j) {
				if(digraph.getAdjacencyMatrix()[nextNode][j]) {
					pool.get(j).addAll(outPool);
				}
			}			
			
			// and push down our weight vector to all predecessors!
			pushDown(weights, nextNode, digraph);
			
			taken[nextNode] = true;
			--nodesLeft;
		} // nodesLeft > 0 
		
		// finally fill up disconnected components' utilities
		for(int i = 0; i < nextUtility; ++i) {
			maxWeights[i] += 1; // to make sure we have greater than max
		}
		
		for(int i = 0; i < digraph.size(); ++i) {
			for(int j = 0; j < nextUtility; ++j) {
				if(weights[i][j] == 0)
					weights[i][j] = maxWeights[j];
			}
		}
		return new UtilityStructure(nextUtility, weights);
	}


	private void pushDown(int[][] weights, int nextNode, DirectedGraph digraph) {
		for(int i = 0; i < digraph.size(); ++i) {
			if(digraph.getAdjacencyMatrix()[i][nextNode]) {
				for(int j = 0; j < digraph.size(); ++j) {
					if(weights[nextNode][j] > 0 && weights[i][j] == 0) {
						weights[i][j] = weights[nextNode][j];
						pushDown(weights, i, digraph);
					}
				}
			}
		}
	}


	private int updateRule(int i) {
		return 1 + i; // now just for SPD
		// could be 1 + 2*i for TPD 
	}
}

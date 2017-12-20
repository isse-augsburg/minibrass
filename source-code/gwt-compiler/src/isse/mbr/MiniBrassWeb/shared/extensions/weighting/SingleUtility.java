package isse.mbr.MiniBrassWeb.shared.extensions.weighting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import isse.mbr.MiniBrassWeb.shared.extensions.domain.DirectedGraph;

public class SingleUtility {
	public Map<Integer, Integer> findUtilities(DirectedGraph digraph) {
		Map<Integer, Integer> utilities = new HashMap<>();
		int initValue = 1;

		boolean[][] adjacencyCopy = new boolean[digraph.getAdjacencyMatrix().length][];
		for (int i = 0; i < digraph.getAdjacencyMatrix().length; i++) {
			boolean[] adjacency = digraph.getAdjacencyMatrix()[i];
			adjacencyCopy[i] = Arrays.copyOf(adjacency, adjacency.length);
		}

		int[] weights = new int[digraph.size()];
		// do some kind of top sort here
		int nodesLeft = digraph.size();
		while (nodesLeft > 0) {
			int nextNode = 0;
			for (int i = 0; i < digraph.size(); ++i) {
				if (weights[i] > 0)
					continue;

				boolean hasIncoming = false;
				for (int j = 0; j < digraph.size(); ++j) {
					if (adjacencyCopy[j][i]) {
						hasIncoming = true;
						break;
					}
				}

				if (hasIncoming)
					continue;
				else {
					nextNode = i;
					for (int j = 0; j < digraph.size(); ++j) {
						adjacencyCopy[nextNode][j] = false;
					}
					break;
				}
			}
			// now sum up all the weights of the incomings (in the original graph)
			int weight = initValue;

			int maxChildWeight = 0;

			for (int j = 0; j < digraph.size(); ++j) {
				if (digraph.getAdjacencyMatrix()[j][nextNode]) {
					maxChildWeight = weights[j] > maxChildWeight ? weights[j] : maxChildWeight;
				}
			}
			weight += maxChildWeight;
			weights[nextNode] = weight;
			utilities.put(nextNode, weight);
			--nodesLeft;
		}
		return utilities;
	}
}

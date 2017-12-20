package isse.mbr.MiniBrassWeb.shared.extensions.domain;

import isse.mbr.MiniBrassWeb.shared.graph.DGraph;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

public class DirectedGraph {
	private boolean[][] adjacencyMatrix;

	public DirectedGraph(int n) {
		adjacencyMatrix = new boolean[n][n];
	}

	public void markEdge(int i, int j) {
		adjacencyMatrix[i][j] = true;
	}

	public int size() {
		return adjacencyMatrix.length;
	}

	public boolean[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public void parse(String input) throws MiniBrassParseException {
		// TODO for now, we expect a literal graph in the form "[| 2, 1 | 1, 0
		// |]"
		// assumes strictly that soft constraints are labeled from 1 to nScs
		String processed = input.replaceAll("\\[\\|", "");
		processed = processed.replaceAll("\\|\\]", "").trim();
		processed = processed.replaceAll("transClosureWrap", "");
		processed = processed.replaceAll("\\(", "");
		processed = processed.replaceAll("\\)", "");

		String[] tok = processed.split("\\|");

		for (String nextEdge : tok) {
			nextEdge = nextEdge.trim();

			String[] splitted = nextEdge.split(",");
			if (splitted.length != 2) {
				throw new MiniBrassParseException("Error: An edge must contain of precisely two nodes.");
			}
			int fromId = Integer.parseInt(splitted[0].trim()) - 1;
			int toId = Integer.parseInt(splitted[1].trim()) - 1;
			markEdge(fromId, toId);
		}
	}

	public void transcribe(DGraph<Integer> jgraph) throws MiniBrassParseException {
		for (int v = 1; v <= adjacencyMatrix.length; ++v) {
			jgraph.addVertex(v);
		}

		for (int i = 0; i < adjacencyMatrix.length; ++i) {
			for (int j = 0; j < adjacencyMatrix.length; ++j) {

				try {
					if (adjacencyMatrix[i][j]) {
						jgraph.addEdge(i + 1, j + 1);
					}
				} catch (IllegalArgumentException ce) {
					throw new MiniBrassParseException(
							"Detected cyclic edge when adding edge : " + (i + 1) + " -> " + (j + 1));
				}
			}
		}

	}
}

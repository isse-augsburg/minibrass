package isse.mbr.extensions.domain;

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
}

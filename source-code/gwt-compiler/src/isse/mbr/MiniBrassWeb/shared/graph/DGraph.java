package isse.mbr.MiniBrassWeb.shared.graph;

import java.util.HashSet;
import java.util.Set;

public class DGraph<V> {
	private Set<V> vertices = new HashSet<>();
	private Set<DEdge<V>> edges = new HashSet<>();

	public void addVertex(V vertex) {
		vertices.add(vertex);
	}

	public void addEdge(V from, V to) {
		edges.add(new DEdge<V>(from, to));
	}

	public boolean containsVertex(V vertex) {
		return vertices.contains(vertex);
	}

	public Set<DEdge<V>> edgeSet() {
		return edges;
	}

	public Set<V> vertexSet() {
		return vertices;
	}

	public Set<DEdge<V>> edgesOf(V vertex) {
		Set<DEdge<V>> ret = new HashSet<>();
		for (DEdge<V> edge : edges)
			if (edge.getFrom() == vertex || edge.getTo() == vertex)
				ret.add(edge);
		return ret;
	}

	public Set<V> edgeTargets(V vertex) {
		Set<V> ret = new HashSet<>();
		for (DEdge<V> edge : edges)
			if (edge.getFrom() == vertex)
				ret.add(edge.getTo());
		return ret;
	}

	public V getEdgeSource(DEdge<V> edge) {
		return edge.getFrom();
	}

	public int getEdgeTarget(DEdge<Integer> edge) {
		return edge.getTo();
	}

	public void closeTransitive() {
		boolean progress = true;
		while (progress) {
			progress = false;
			start: for (V vertex : vertexSet()) {
				Set<V> ts = edgeTargets(vertex);
				for (V t : ts) {
					for (V tt : edgeTargets(t)) {
						if (!ts.contains(tt)) {
							addEdge(vertex, tt);
							progress = true;
							break start;
						}
					}
				}
			}
		}
	}

}
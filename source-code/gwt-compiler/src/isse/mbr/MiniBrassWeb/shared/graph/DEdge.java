package isse.mbr.MiniBrassWeb.shared.graph;

public class DEdge<T> {
	private T from;
	private T to;

	public DEdge(T from, T to) {
		this.from = from;
		this.to = to;
	}

	public T getFrom() {
		return from;
	}

	public T getTo() {
		return to;
	}

}

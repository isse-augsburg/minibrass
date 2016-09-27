package isse.mbr.extensions.domain;

public class UtilityStructure implements LabelSource {
	private int size; // number of utility functions
	
	public UtilityStructure(int size, int[][] weights) {
		super();
		this.size = size;
		this.weights = weights;
	}
	
	private int[][] weights;
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int[][] getWeights() {
		return weights;
	}
	public void setWeights(int[][] weights) {
		this.weights = weights;
	}
	@Override
	public String getLabel(int index) {
		StringBuilder sb = new StringBuilder("[ ");

		for(int i = 0; i < getSize(); ++i) {
			if(i > 0)
				sb.append(", ");
			
			sb.append( getWeights()[index][i]);
		}
		sb.append(" ]");
		return sb.toString();
	}
	
	
}

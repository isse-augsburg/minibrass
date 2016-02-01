package isse.mbr.model.types;

public class FloatType implements PrimitiveType {
	double lower;
	double upper;
	boolean bounded;
	
	public FloatType() {
		bounded = false;
	}

	public FloatType(double lower, double upper) {
		super();
		this.lower = lower;
		this.upper = upper;
		bounded = true;
	}
	
	@Override
	public String toString() {
		return "float" + (bounded ? "("+lower+" .. "+upper + ")" : "" );
	}
}

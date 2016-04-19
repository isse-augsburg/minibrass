package isse.mbr.model.types;

public class FloatType implements PrimitiveType {
	NamedRef<Double> lower;
	NamedRef<Double> upper;
	boolean bounded;
	
	public FloatType() {
		bounded = false;
	}

	public FloatType(NamedRef<Double> lower, NamedRef<Double> upper) {
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

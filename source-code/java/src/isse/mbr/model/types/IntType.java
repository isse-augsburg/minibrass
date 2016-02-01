package isse.mbr.model.types;

public class IntType implements PrimitiveType {
	int lower;
	int upper;
	boolean bounded;
	
	public IntType() {
		bounded = false;
	}

	public IntType(int lower, int upper) {
		super();
		this.lower = lower;
		this.upper = upper;
		bounded = true;
	}
	
	@Override
	public String toString() {
		return "int" + (bounded ? "("+lower+" .. "+upper + ")" : "" );
	}
}

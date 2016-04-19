package isse.mbr.model.types;

public class IntType implements PrimitiveType {
	NamedRef<Integer> lower;
	NamedRef<Integer> upper;
	boolean bounded;
	
	public IntType() {
		bounded = false;
	}

	public IntType(NamedRef<Integer> lower, NamedRef<Integer> upper) {
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

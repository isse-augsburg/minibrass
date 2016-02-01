package isse.mbr.model.types;

/**
 * Represents a set of x in MiniZinc
 * @author Alexander Schiendorfer
 *
 */
public class SetType implements MiniZincVarType {
	private PrimitiveType decoratedType;

	public SetType(PrimitiveType decoratedType) {
		super();
		this.decoratedType = decoratedType;
	}
	
	@Override
	public String toString() {
		return "set of "+decoratedType;
	}
}

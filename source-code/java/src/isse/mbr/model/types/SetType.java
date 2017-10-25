package isse.mbr.model.types;

import isse.mbr.model.parsetree.AbstractPVSInstance;

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

	@Override
	public String toMzn(AbstractPVSInstance instance) {
		return "set of "+ decoratedType.toMzn(instance);
	}
}

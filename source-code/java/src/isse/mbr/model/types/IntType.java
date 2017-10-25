package isse.mbr.model.types;

import isse.mbr.model.parsetree.AbstractPVSInstance;

public class IntType implements PrimitiveType {
	
	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public String toMzn(AbstractPVSInstance instance) {
		return "int";
	}
}

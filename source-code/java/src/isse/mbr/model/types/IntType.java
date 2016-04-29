package isse.mbr.model.types;

import isse.mbr.model.parsetree.PVSInstance;

public class IntType implements PrimitiveType {
	
	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public String toMzn(PVSInstance instance) {
		return "int";
	}
}

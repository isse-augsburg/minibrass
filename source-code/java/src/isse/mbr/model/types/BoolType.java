package isse.mbr.model.types;

import isse.mbr.model.parsetree.PVSInstance;

public class BoolType implements PrimitiveType {

	@Override
	public String toString() {
		return "bool";
	}
	
	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public String toMzn(PVSInstance instance) {
		return "bool";
	}
}

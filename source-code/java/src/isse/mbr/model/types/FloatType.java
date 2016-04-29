package isse.mbr.model.types;

import isse.mbr.model.parsetree.PVSInstance;

public class FloatType implements PrimitiveType {

	@Override
	public String toString() {
		return "float";
	}
	
	@Override
	public boolean isFloat() {
		return true;
	}

	@Override
	public String toMzn(PVSInstance instance) {
		return "float";
	}
}

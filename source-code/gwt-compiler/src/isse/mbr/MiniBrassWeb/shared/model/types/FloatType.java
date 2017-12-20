package isse.mbr.MiniBrassWeb.shared.model.types;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;

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
	public String toMzn(AbstractPVSInstance instance) {
		return "float";
	}
}

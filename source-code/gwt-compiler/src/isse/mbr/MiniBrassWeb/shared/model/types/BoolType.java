package isse.mbr.MiniBrassWeb.shared.model.types;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;

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
	public String toMzn(AbstractPVSInstance instance) {
		return "bool";
	}
}

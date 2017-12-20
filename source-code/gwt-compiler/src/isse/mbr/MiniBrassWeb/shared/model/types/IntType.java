package isse.mbr.MiniBrassWeb.shared.model.types;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;

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

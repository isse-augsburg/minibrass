package isse.mbr.model.types;

import isse.mbr.model.parsetree.AbstractPVSInstance;

public class StringType implements PrimitiveType {

	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public String toMzn(AbstractPVSInstance instance) {
		return "string";
	}
}

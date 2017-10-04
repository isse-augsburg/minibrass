package isse.mbr.model.types;

import java.util.LinkedList;
import java.util.List;

import isse.mbr.model.parsetree.PVSInstance;

public class MultiSetType implements MiniZincArrayLike {
	private PrimitiveType elementType;
	private NumericValue maxMultiplicity;
	private ArrayType arrayType;
	
	public MultiSetType(NumericValue maxMultiplicity, PrimitiveType decoratedType) {
		super();
		this.maxMultiplicity = maxMultiplicity;
		this.elementType = decoratedType;
		this.arrayType = null;
	}
	
	@Override
	public String toString() {
		return "mset["+maxMultiplicity+"] of "+elementType;
	}

	@Override
	public String toMzn(PVSInstance instance) {
		return "array["+ elementType.toMzn(instance)+"] of var 0.."+maxMultiplicity.toMiniZinc(instance);
	}

	public PrimitiveType getElementType() {
		return elementType;
	}

	public void setElementType(PrimitiveType elementType) {
		this.elementType = elementType;
	}

	public ArrayType getEncodedArray() {
		if(arrayType == null) {
			List<PrimitiveType> indexSets = new LinkedList<>();
			indexSets.add(getElementType());
			arrayType = new ArrayType(new IntervalType(new NumericValue(0), maxMultiplicity), indexSets);
		}
		return arrayType;
	}
	
	public NumericValue getMaxMultiplicity() {
		return maxMultiplicity;
	}

	public void setMaxMultiplicity(NumericValue maxMultiplicity) {
		this.maxMultiplicity = maxMultiplicity;
	}

	@Override
	public ArrayType getArrayType() {
		return getEncodedArray();
	}

}

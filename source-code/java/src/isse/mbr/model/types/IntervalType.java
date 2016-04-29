package isse.mbr.model.types;

import java.util.ArrayList;
import java.util.Collection;

import isse.mbr.model.parsetree.PVSInstance;

public class IntervalType implements PrimitiveType {
	private NumericValue lower;
	private NumericValue upper;
	private PrimitiveType superSort;
	private Collection<String> referencedParameters;
	
	public IntervalType(NumericValue lower, NumericValue upper) {
		this.lower = lower;
		this.upper = upper;
		this.superSort = null;
	}
	public NumericValue getLower() {
		return lower;
	}
	public void setLower(NumericValue lower) {
		this.lower = lower;
	}
	public NumericValue getUpper() {
		return upper;
	}
	public void setUpper(NumericValue upper) {
		this.upper = upper;
	}	

	public String toString() {
		return "int" + "("+lower+" .. "+upper + ")";
	}
	
	/**
	 * Resolves the parameter references of lower and upper (if applicable)
	 * and returns either a FloatType or an IntType
	 * @return
	 */
	
	public PrimitiveType getSuperSort() {
		if(superSort == null) { // evaluate it first 
			if(isFloat()) {
				superSort = new FloatType();
			} else {
				superSort = new IntType();
			}
		}
		return superSort;
	}
	
	public Collection<String> getReferencedParameters() {
		if(referencedParameters == null) {
			referencedParameters = new ArrayList<>(2);
			if(upper.getReferencedParameter() != null) {
				NamedRef<PVSParameter> referencedPar = upper.getReferencedParameter();
				referencedParameters.add(referencedPar.name);
			}
			if(lower.getReferencedParameter() != null) {
				NamedRef<PVSParameter> referencedPar = lower.getReferencedParameter();
				referencedParameters.add(referencedPar.name);
			}
		}
		return referencedParameters;
	}
	
	@Override
	public boolean isFloat() {
		return lower.isFloat() || upper.isFloat();
	}
	@Override
	public String toMzn(PVSInstance instance) {
		return lower.toMiniZinc(instance) + ".." + upper.toMiniZinc(instance);
	}
}

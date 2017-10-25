package isse.mbr.model.types;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.parsing.CodeGenerator;

/**
 * Numeric value can either be a float or int literal or it is a named reference to a PVS parameter
 * @author Alexander Schiendorfer
 *
 */
public class NumericValue {
	private Integer intValue;
	private Double floatValue;
	private NamedRef<PVSFormalParameter> referencedParameter;
	
	public NumericValue(int intVal) {
		this.intValue = intVal;
		this.floatValue = null;
		this.referencedParameter = null;
	}
	
	public NumericValue(double floatVal) {
		this.intValue = null;
		this.floatValue = floatVal;
		this.referencedParameter = null;
	}
	
	public NumericValue(String paramName) {
		this.intValue = null;
		this.floatValue = null;
		this.referencedParameter = new NamedRef<>(paramName);
	}
	
	public NumericValue(PVSFormalParameter param) {
		this.intValue = null;
		this.floatValue = null;
		this.referencedParameter = new NamedRef<>(param);
		this.referencedParameter.name = param.getName();
	}
	
	public Integer getIntValue() {
		return intValue;
	}
	
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	
	public Double getFloatValue() {
		return floatValue;
	}
	
	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}
	
	public NamedRef<PVSFormalParameter> getReferencedParameter() {
		return referencedParameter;
	}
	
	public void setReferencedParameter(NamedRef<PVSFormalParameter> referencedParameter) {
		this.referencedParameter = referencedParameter;
	}

	public boolean isFloat() {
		if(floatValue != null)
			return true; 
		else if(getIntValue() != null) {
			return false;
		} else {
			PVSFormalParameter par = referencedParameter.instance;
			PrimitiveType  pt = (PrimitiveType) par.getType();
			return pt.isFloat();

		}
	}
	
	
	@Override
	public String toString() {
		if(intValue != null) 
			return intValue.toString();
		else if (floatValue != null)
			return floatValue.toString();
		else 
			return referencedParameter.toString();
	}

	public String toMiniZinc(AbstractPVSInstance instance) {
		if(floatValue != null)
			return Double.toString(floatValue); 
		else if(getIntValue() != null) {
			return Integer.toString(intValue); 
		} else {
			PVSFormalParameter par = referencedParameter.instance;			
			return CodeGenerator.encodeIdent(par, instance);

		}
	}
}

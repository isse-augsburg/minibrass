package isse.mbr.model.parsetree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSType;

public class PVSInstance extends AbstractPVSInstance {
	private NamedRef<PVSType> type;
	private Map<String, String> parameterValues;
	private Map<String, Map<String,String>> parameterArrayValues; // there is one map for every array-typed parameter
	private Map<String, SoftConstraint> softConstraints;
	private Map<String, PVSParamInst> parametersLinked; // gets done by semantic checker
	
	private int numberScs;
	
	public PVSInstance() {
		parameterValues = new HashMap<>();
		softConstraints = new LinkedHashMap<>(); 
		parameterArrayValues = new HashMap<>();
	}
	
	public NamedRef<PVSType> getType() {
		return type;
	}
	
	public void setType(NamedRef<PVSType> type) {
		this.type = type;
	}

	public Map<String,String> getParameterValues() {
		return parameterValues;
	}
	

	public int getNumberSoftConstraints() {
		return numberScs;
	}

	public void setNumberSoftConstraints(int numberScs) {
		this.numberScs = numberScs;
	}
	
	@Override
	public String toString() {
		return name + ": "+type + ", nScs: "+numberScs+ ", params: "+Arrays.toString(parameterValues.entrySet().toArray()); 
	}

	public Map<String, PVSParamInst> getParametersLinked() {
		return parametersLinked;
	}

	public void setParametersLinked(Map<String, PVSParamInst> parametersLinked) {
		this.parametersLinked = parametersLinked;
	}

	public Map<String, SoftConstraint> getSoftConstraints() {
		return softConstraints;
	}

	public Map<String, Map<String, String>> getParameterArrayValues() {
		return parameterArrayValues;
	}

	/**
	 * This is essentially a template method for, e.g., performing updates on par-insts when using a morphisms
	 */
	public void update() {
		// do nothing here
	}

}

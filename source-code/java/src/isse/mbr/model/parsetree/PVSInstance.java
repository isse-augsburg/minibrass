package isse.mbr.model.parsetree;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSType;

public class PVSInstance extends AbstractPVSInstance {
	private NamedRef<PVSType> type;
	private Map<String, String> parameterValues;
	private Map<String, SoftConstraint> softConstraints;
	private Map<String, PVSParamInst> parametersLinked; // gets done by semantic checker
	
	private int numberScs;
	
	public PVSInstance() {
		parameterValues = new HashMap<>();
		softConstraints = new HashMap<>();
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

}

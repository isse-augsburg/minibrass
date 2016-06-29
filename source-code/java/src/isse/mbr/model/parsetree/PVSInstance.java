package isse.mbr.model.parsetree;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSType;

public class PVSInstance extends AbstractPVSInstance {
	private NamedRef<PVSType> type;
	private Collection<String> parameterValues;
	private Map<String, PVSParamInst> parametersLinked; // gets done by semantic checker
	
	private int numberScs;
	
	public PVSInstance() {
		parameterValues = new LinkedList<>();
	}
	
	public NamedRef<PVSType> getType() {
		return type;
	}
	
	public void setType(NamedRef<PVSType> type) {
		this.type = type;
	}

	public Collection<String> getParameterValues() {
		return parameterValues;
	}
	
	public void setParameterValues(Collection<String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public int getNumberSoftConstraints() {
		return numberScs;
	}

	public void setNumberSoftConstraints(int numberScs) {
		this.numberScs = numberScs;
	}
	
	@Override
	public String toString() {
		return name + ": "+type + ", nScs: "+numberScs+ ", params: "+Arrays.toString(parameterValues.toArray()); 
	}

	public Map<String, PVSParamInst> getParametersLinked() {
		return parametersLinked;
	}

	public void setParametersLinked(Map<String, PVSParamInst> parametersLinked) {
		this.parametersLinked = parametersLinked;
	}
}

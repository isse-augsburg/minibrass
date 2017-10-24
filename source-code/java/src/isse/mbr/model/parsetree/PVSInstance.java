package isse.mbr.model.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSParameter;
import isse.mbr.model.types.PVSType;

public class PVSInstance extends AbstractPVSInstance {
	private NamedRef<PVSType> type;
	private Map<String, String> parameterValues;
	private Map<String, Map<String,String>> parameterArrayValues; // there is one map for every array-typed parameter
	private Map<String, SoftConstraint> softConstraints;
	protected Map<String, PVSParamInst> parametersInstantiated; // gets done by semantic checker
	protected Map<String, String> generatedCodeParameters; // for external morphisms
	
	private int numberScs;
	
	public PVSInstance() {
		parameterValues = new HashMap<>();
		softConstraints = new LinkedHashMap<>(); 
		parameterArrayValues = new HashMap<>();
		generatedCodeParameters = new HashMap<>();
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

	public Map<String, PVSParamInst> getParametersInstantiated() {
		return parametersInstantiated;
	}

	public void setParametersInstantiated(Map<String, PVSParamInst> parametersLinked) {
		this.parametersInstantiated = parametersLinked;
	}

	public Map<String, SoftConstraint> getSoftConstraints() {
		return softConstraints;
	}

	public Map<String, Map<String, String>> getParameterArrayValues() {
		return parameterArrayValues;
	}

	public List<PVSParameter> getInstanceParameters() {
		return type.instance.getPvsParameters();
	}

	public Map<String, String> getGeneratedCodeParameters() {
		return generatedCodeParameters;
	}

	public void setGeneratedCodeParameters(Map<String, String> generatedCodeParameters) {
		this.generatedCodeParameters = generatedCodeParameters;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public Collection<AbstractPVSInstance> getChildren() {
		return new ArrayList<>(); // for now this is really an empty list, we should make sure that this is basically not called
	}

}

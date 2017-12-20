package isse.mbr.MiniBrassWeb.shared.model.parsetree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import isse.mbr.MiniBrassWeb.shared.model.types.NamedRef;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSFormalParameter;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSParamInst;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSType;

/**
 * This class represents one specific instance of a PVS type that 
 * is used in a MiniBrass model; The parameter values refer to the 
 * actual parameters supplied during instantiation for the formally
 * defined parameters of a PVS type 
 * 
 * @author Alexander Schiendorfer
 *
 */
public class PVSInstance extends AbstractPVSInstance {
	private NamedRef<PVSType> type;
	private Map<String, String> actualParameterValues;
	private Map<String, Map<String,String>> parameterArrayValues; // there is one map for every array-typed parameter
	private Map<String, SoftConstraint> softConstraints;
	protected Map<String, PVSParamInst> checkedParameters; // gets done by semantic checker
	protected Map<String, String> generatedCodeParameters; // for external morphisms
	
	private int numberScs;
	
	public PVSInstance() {
		actualParameterValues = new HashMap<>();
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

	public Map<String,String> getActualParameterValues() {
		return actualParameterValues;
	}
	

	public int getNumberSoftConstraints() {
		return numberScs;
	}

	public void setNumberSoftConstraints(int numberScs) {
		this.numberScs = numberScs;
	}
	
	@Override
	public String toString() {
		return name + ": "+type + ", nScs: "+numberScs+ ", params: "+Arrays.toString(actualParameterValues.entrySet().toArray()); 
	}

	public Map<String, PVSParamInst> getCheckedParameters() {
		return checkedParameters;
	}

	public void setCheckedParameters(Map<String, PVSParamInst> checkedParameters) {
		this.checkedParameters = checkedParameters;
	}

	public Map<String, SoftConstraint> getSoftConstraints() {
		return softConstraints;
	}

	public Map<String, Map<String, String>> getParameterArrayValues() {
		return parameterArrayValues;
	}

	public List<PVSFormalParameter> getInstanceParameters() {
		return type.instance.getFormalParameters();
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

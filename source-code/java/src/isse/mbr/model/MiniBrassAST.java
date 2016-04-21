package isse.mbr.model;

import java.util.HashMap;
import java.util.Map;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSType;

/**
 * A full soft constraint model in MiniBrass
 * that consists of PVS types, instantiations of
 * PVS, their algebraic combinations and a main PVS
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassAST {

	private Map<String, PVSType> pvsTypes;
	private Map<String, AbstractPVSInstance> pvsInstances; // here by identifier
	private Map<String, AbstractPVSInstance> pvsReferences; // here by reference
	

	private NamedRef<AbstractPVSInstance> solveInstance;
	
	public MiniBrassAST() {
		pvsTypes = new HashMap<String, PVSType>();
		pvsInstances = new HashMap<>();
		pvsReferences =  new HashMap<>();
	}
	
	public void registerPVSType(String reference, PVSType type) {
		System.out.println("Registering PVS type ... "+ type);
		pvsTypes.put(reference, type);
	}

	public NamedRef<AbstractPVSInstance> getSolveInstance() {
		return solveInstance;
	}

	public void setSolveInstance(NamedRef<AbstractPVSInstance> solveInstance) {
		this.solveInstance = solveInstance;
	}

	public Map<String, AbstractPVSInstance> getPvsInstances() {
		return pvsInstances;
	}

	public void setPvsInstances(Map<String, AbstractPVSInstance> pvsInstances) {
		this.pvsInstances = pvsInstances;
	}

	public Map<String, AbstractPVSInstance> getPvsReferences() {
		return pvsReferences;
	}

	public void setPvsReferences(Map<String, AbstractPVSInstance> pvsReferences) {
		this.pvsReferences = pvsReferences;
	}

	public Map<String, PVSType> getPvsTypes() {
		return pvsTypes;
	}

	public void setPvsTypes(Map<String, PVSType> pvsTypes) {
		this.pvsTypes = pvsTypes;
	}
}


package isse.mbr.model;

import java.util.HashMap;
import java.util.Map;

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
	
	public MiniBrassAST() {
		pvsTypes = new HashMap<String, PVSType>();
	}
	
	public void registerPVSType(String reference, PVSType type) {
		System.out.println("Registering PVS type ... "+ type);
		pvsTypes.put(reference, type);
	}
}


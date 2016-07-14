package isse.mbr.model.types;

import isse.mbr.model.parsetree.PVSInstance;

/**
 * MiniZinc types than can be used as parameters
 * 
 * - all var types
 * - arrays of var types
 * @author Alexander Schiendorfer
 *
 */
public interface MiniZincParType {
	String toMzn(PVSInstance instance);
}

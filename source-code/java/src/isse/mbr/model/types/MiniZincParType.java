package isse.mbr.model.types;

import isse.mbr.model.parsetree.PVSInstance;

/**
 * MiniZinc types than can be used as parameters
 * Intended as a purely virtual interface - each type
 * shall either be
 * 
 * - all var types (MiniZincVarType)
 * - arrays of var types, multisets (MiniZincArrayLike)
 * @author Alexander Schiendorfer
 *
 */
public interface MiniZincParType {
	String toMzn(PVSInstance instance);
}

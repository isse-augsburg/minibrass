package isse.mbr.model.types;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an array[index_set] of MiniZincVarType
 * @author Alexander Schiendorfer
 *
 */
public class ArrayType implements MiniZincParType {

	private MiniZincVarType type;
	private List<IntType> indexSets;
	
	public ArrayType() {
		indexSets = new LinkedList<>();
	}
	
	public ArrayType(MiniZincVarType type, List<IntType> indexSets) {
		super();
		this.type = type;
		this.indexSets = indexSets;
	}
	public MiniZincVarType getType() {
		return type;
	}
	public void setType(MiniZincVarType type) {
		this.type = type;
	}
	public List<IntType> getIndexSets() {
		return indexSets;
	}
	public void setIndexSets(List<IntType> indexSets) {
		this.indexSets = indexSets;
	} 
}

package isse.mbr.model.types;

import java.util.LinkedList;
import java.util.List;

import isse.mbr.model.parsetree.PVSInstance;

/**
 * Represents an array[index_set] of MiniZincVarType
 * @author Alexander Schiendorfer
 *
 */
public class ArrayType implements MiniZincParType {

	private MiniZincVarType type;
	private List<PrimitiveType> indexSets;
	
	public ArrayType() {
		indexSets = new LinkedList<>();
	}
	
	public ArrayType(MiniZincVarType type, List<PrimitiveType> indexSets) {
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
	public List<PrimitiveType> getIndexSets() {
		return indexSets;
	}
	public void setIndexSets(List<PrimitiveType> indexSets) {
		this.indexSets = indexSets;
	}

	@Override
	public String toMzn(PVSInstance instance) {
		StringBuilder arrayBuilder = new StringBuilder("array[");
		boolean first = true;
		for(PrimitiveType indexType : indexSets) {
			if(first)
				first = false;
			else 
				arrayBuilder.append(", ");
			arrayBuilder.append(indexType.toMzn(instance));
		}
		arrayBuilder.append("] of ");
		arrayBuilder.append(type.toMzn(instance));
		return arrayBuilder.toString();
	} 
	
}

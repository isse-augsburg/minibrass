package isse.mbr.model.types;

import java.util.LinkedList;
import java.util.List;

import isse.mbr.model.parsetree.AbstractPVSInstance;

/**
 * Represents an array[index_set] of MiniZincVarType
 * @author Alexander Schiendorfer
 *
 */
public class ArrayType implements MiniZincArrayLike {

	private MiniZincVarType elementType;
	private List<PrimitiveType> indexSets;
	private List<MiniZincVarType> pendingIndexTypes;
	
	public ArrayType() {
		indexSets = new LinkedList<>();
	}
	
	public ArrayType(MiniZincVarType type, List<PrimitiveType> indexSets) {
		super();
		this.elementType = type;
		this.indexSets = indexSets;
	}
	public MiniZincVarType getElementType() {
		return elementType;
	}
	public void setElementType(MiniZincVarType type) {
		this.elementType = type;
	}
	public List<PrimitiveType> getIndexSets() {
		return indexSets;
	}
	public void setIndexSets(List<PrimitiveType> indexSets) {
		this.indexSets = indexSets;
	}

	@Override
	public String toMzn(AbstractPVSInstance instance) {
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
		arrayBuilder.append(elementType.toMzn(instance));
		return arrayBuilder.toString();
	}

	public void setPendingIndexTypes(List<isse.mbr.model.types.MiniZincVarType> pendingIndexTypes) {
		this.pendingIndexTypes = pendingIndexTypes;
	}

	public List<MiniZincVarType> getPendingIndexTypes() {
		return pendingIndexTypes;
	}

	@Override
	public ArrayType getArrayType() {
		return this;
	} 
	
}

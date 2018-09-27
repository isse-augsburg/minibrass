package isse.mbr.tools.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import isse.mbr.model.types.IntervalType;

/**
 * Represents a multidimensional array in MiniZinc
 * @author alexander
 *
 */
public class MiniZincTensor implements Serializable {

	private int dimension;
	private List<IntervalType> indexSets;
	private List<MiniZincVariable> flatValues;
	/**
	 * 
	 */
	private static final long serialVersionUID = 567284593086372503L;

	public MiniZincTensor()  {
		indexSets = new ArrayList<>();
		flatValues = new LinkedList<>();
	}
	
	public void addIndexSet(IntervalType it) {
		this.indexSets.add(it);
	}
	
	public void addFlatValue(MiniZincVariable variable) {
		flatValues.add(variable);
	}
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	
	/**
	 * Variable access function (e.g., if we start from 0 to 2, we must address within [0,2], for 1..3, we start at 1)
	 * @param i refers to the logical index according to the index set 
	 * @return
	 */
	public MiniZincVariable get(int i) {
		// assert dim = 1
		if(dimension != 1)
			throw new RuntimeException("Cannot access a multidimensional array with one index");
		IntervalType firstAxis = indexSets.get(0);
		int lower = firstAxis.getLower().getIntValue();
		
		return flatValues.get(i-lower);
	}

	public MiniZincVariable get(int i, int j) {
		// assert dim = 2
		if(dimension != 2)
			throw new RuntimeException("Cannot access a non-2d array with two indices ");
		IntervalType firstAxis = indexSets.get(0);
		IntervalType secondAxis = indexSets.get(1);
		int noCols = secondAxis.getUpper().getIntValue()-secondAxis.getLower().getIntValue() + 1;
		
		int rowIndex = i - firstAxis.getLower().getIntValue();
		int colIndex = j - secondAxis.getLower().getIntValue();
		
		return flatValues.get(rowIndex*noCols + colIndex);
	}
	
	public  MiniZincVariable get(int i, int j, int k) {
		// assert dim = 3
		throw new NotImplementedException("Dimensions higher than 2 need to be implemented");
	}
	
	public MiniZincVariable get(int i, int j, int k, int l) {
		// assert dim = 4
		throw new NotImplementedException("Dimensions higher than 2 need to be implemented");
	}
	
	public MiniZincVariable get(int i, int j, int k, int l, int m) {
		// assert dim = 5
		throw new NotImplementedException("Dimensions higher than 2 need to be implemented");
	}
	
	public MiniZincVariable get(int i, int j, int k, int l, int m, int n) {
		// assert dim = 6
		throw new NotImplementedException("Dimensions higher than 2 need to be implemented");
	}

	@Override
	public String toString() {
		
		return "Array with dim: "+dimension + " : " + Arrays.toString(flatValues.toArray());
	}
}

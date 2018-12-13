package isse.mbr.tools.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionToIntMapper<T> {

	private Map<Integer, T> fromIntIdToLogicalId;
	private Map<T, Integer> fromLogicalIdToIntId;

	public CollectionToIntMapper(T[] sourceElements) {
		this(Arrays.asList(sourceElements));
	}

	public CollectionToIntMapper(List<T> sourceElements) {
		super();
		fromIntIdToLogicalId = new HashMap<>();
		fromLogicalIdToIntId = new HashMap<>();

		for (int i = 0; i < sourceElements.size(); ++i) {
			fromLogicalIdToIntId.put(sourceElements.get(i), i + 1);
			fromIntIdToLogicalId.put(i + 1, sourceElements.get(i));
		}
	}

	public int getIntIdForElement(T element) {
		return fromLogicalIdToIntId.get(element);
	}

	public T getElementForIntId(int intId) {
		return fromIntIdToLogicalId.get(intId);
	}
}

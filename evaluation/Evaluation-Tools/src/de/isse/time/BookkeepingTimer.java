package de.isse.time;

/**
 * Simple utility that allows to keep time for multiple categories
 * @author Alexander Schiendorfer
 *
 */
public class BookkeepingTimer {
	private static final int MAX_IND = 15;
	private long startTimes[];
	private long endTimes[];

	public BookkeepingTimer() {
		this(MAX_IND);
	}
	
	public BookkeepingTimer(int maxInd) {
		startTimes = new long[maxInd + 1];
		endTimes = new long[maxInd + 1];
	}

	public void tick(int ident) {
		startTimes[ident] = System.nanoTime();
	}

	public long tock(int ident) {
		endTimes[ident] = System.nanoTime();
		return endTimes[ident] - startTimes[ident];
	}

	public double getElapsedSecs(int ident) {
		long elapsed = endTimes[ident]
				- startTimes[ident];
		return ((double) elapsed) * 1.0e-9;
	}
}

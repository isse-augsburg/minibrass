package isse.mbr.tools;

/**
 * Generic listener interface to facilitate parsing MiniZinc files
 * @author Alexander Schiendorfer
 *
 */
public interface MiniZincResultListener {
	void notifyOptimality(); // called if a solution was optimal
	void notifyLine(String line); // a result line 
	void notifyOutput(String output); // the whole written and formatted output
	void notifySolved();
}

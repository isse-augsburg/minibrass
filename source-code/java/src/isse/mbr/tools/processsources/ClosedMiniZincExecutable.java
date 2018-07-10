package isse.mbr.tools.processsources;

import isse.mbr.tools.MiniZincProcessSource;

/**
 * A template that simply invokes a tailor-made mzn executable
 * @author alexander
 *
 */
public class ClosedMiniZincExecutable extends MiniZincProcessSource {
	private String execName;
	
	public static final String CBC = "mzn-cbc";
	public static final String NUMBERJACK = "mzn_numberjack";
	public static final String GECODE = "mzn-gecode";
	
	public ClosedMiniZincExecutable(String execName) {
		this.execName = execName;
	}

	@Override
	public ProcessBuilder getMiniZincProcessBuilder() {
		
		ProcessBuilder pb;
		pb = new ProcessBuilder(execName);
		
		if(useAllSolutions)
			pb.command().add("-a");
		return pb;
	}
	
}

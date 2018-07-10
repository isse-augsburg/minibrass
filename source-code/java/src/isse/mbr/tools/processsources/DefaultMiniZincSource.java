package isse.mbr.tools.processsources;

import isse.mbr.tools.MiniZincProcessSource;

public class DefaultMiniZincSource extends MiniZincProcessSource {

	private boolean useDefault;
	private String flatzincExecutable = "fzn-gecode";
	private String minizincGlobals = "gecode";  
	
	public static final String JACOP_FZN = "fzn-jacop";
	public static final String JACOP_GLOBALS = "jacop";
	
	public static final String GECODE_FZN = "fzn-gecode";
	public static final String GECODE_GLOBALS = "gecode";
	
	
	@Override
	public ProcessBuilder getMiniZincProcessBuilder() {
		ProcessBuilder pb;
		if(useDefault) {
			pb = new ProcessBuilder("minizinc" );
		} else {
			pb = new ProcessBuilder("minizinc",  "-f",flatzincExecutable, "-G"+minizincGlobals);
		}

		if(useAllSolutions)
			pb.command().add("-a");
		return pb;
	}
	
	public String getFlatzincExecutable() {
		return flatzincExecutable;
	}


	public void setFlatzincExecutable(String flatzincExecutable) {
		this.flatzincExecutable = flatzincExecutable;
	}


	public String getMinizincGlobals() {
		return minizincGlobals;
	}


	public void setMinizincGlobals(String minizincGlobals) {
		this.minizincGlobals = minizincGlobals;
	}


	public boolean isUseDefault() {
		return useDefault;
	}


	public void setUseDefault(boolean useDefault) {
		this.useDefault = useDefault;
	}
}

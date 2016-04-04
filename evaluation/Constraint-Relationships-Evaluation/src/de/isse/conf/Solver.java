package de.isse.conf;

public enum Solver {
	
	GECODE("gecode","fzn-gecode"), JACOP("jacop","/home/alexander/Documents/Programs/minizinc/MiniZincIDE-2.0.13-bundle-linux-x86_64/fzn-jacop"), TOULBAR2, OR_TOOLS, CHOCO;

	private String mznGlobal;
	private String fznExec;
	
	Solver (){}
	
	Solver (String mznGlobal, String fznExec) {
		this.mznGlobal=mznGlobal;
		this.fznExec = fznExec;
	}
	
	public String getMznGlobals() {
		return mznGlobal;
	}

	public String getFlatzincExec() {
		return fznExec;
	}
}

package de.isse.conf;

public enum Solver {
	
	GECODE("gecode","fzn-gecode"), JACOP("jacop","fzn-jacop"), TOULBAR2, OR_TOOLS("or-tools","fzn-ort"), CHOCO, G12("g12_fd", "flatzinc");

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

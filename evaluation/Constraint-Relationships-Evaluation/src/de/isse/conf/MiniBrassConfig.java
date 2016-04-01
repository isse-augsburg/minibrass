package de.isse.conf;

public class MiniBrassConfig {
	public SearchType search;
	public Integer timeout; 
	public Boolean mostImportantFirst;

	// if I chose BAB_NON_DOM, I (currently) can't use SPD!
	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"BAB_STRICT", "LNS", "BAB_NATIVE"})
	public Boolean useSPD;
	
	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"BAB_STRICT"})
	public Boolean propagateRedundant; 
	
	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"LNS"})
	public Double lnsProb;

	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"LNS"})
	public Integer lnsIter;
	
	public MiniBrassConfig() {
		// provide default values
		search = SearchType.BAB_NATIVE;		
		timeout = 5000;
		mostImportantFirst = true;
		useSPD = false;
		propagateRedundant = false;
		lnsProb = 0.3;
		lnsIter = 10;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("search: "+search);
		sb.append(", timeout: "+timeout);
		sb.append(", dominance: "+(useSPD ? "SPD" : "TPD") );
		sb.append(", mostImportantFirst: "+mostImportantFirst);
		sb.append(", propagateRedundant: "+propagateRedundant);
		sb.append(", lnsProb: "+lnsProb);
		sb.append(", lnsIter: "+lnsIter);
		return sb.toString();
	}
}

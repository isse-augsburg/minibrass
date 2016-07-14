package de.isse.conf;

import java.io.Serializable;

public class MiniBrassConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5208010108613287651L;
	
	public SearchType search;
	public Integer timeout; // given in *milliseconds* (conforming to MiniSearch)
	public Boolean mostImportantFirst;

	// if I chose BAB_NON_DOM, I (currently) can't use SPD!
	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"BAB_STRICT", "LNS", "BAB_NATIVE"})
	public Boolean useSPD;
	
	@DependsOn(parameter="search",enumClass={SearchType.class}, allowedValues={"BAB_STRICT", "LNS"})
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MiniBrassConfig))
			return false;
		else {
			MiniBrassConfig otherConf = (MiniBrassConfig) obj;
			try {
				return search.equals(otherConf.search) && timeout.equals(otherConf.timeout) && mostImportantFirst.equals(otherConf.mostImportantFirst) && useSPD.equals(otherConf.useSPD)
						&& propagateRedundant.equals(otherConf.propagateRedundant) && lnsIter.equals(otherConf.lnsIter) && lnsProb.equals(otherConf.lnsProb);
			} catch (NullPointerException ne) {
				throw new RuntimeException("There should be no null values! ", ne);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return search.hashCode() + timeout.hashCode() + mostImportantFirst.hashCode() + useSPD.hashCode() + propagateRedundant.hashCode() + lnsIter.hashCode() + lnsProb.hashCode();
	}
	
	public String toFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(search); sb.append("_");
		sb.append(timeout); sb.append("_");
		sb.append((useSPD ? "SPD" : "TPD")); sb.append("_");
		sb.append("MIF_"+(mostImportantFirst ? "1" : "0")); sb.append("_");
		sb.append("PRED_"+(propagateRedundant ? "1" : "0")); sb.append("_");
		String lnsProbStr = Double.toString(lnsProb.doubleValue());
		lnsProbStr = lnsProbStr.replaceAll("\\.", "d");
		sb.append("LNS-PROB_"+lnsProbStr); sb.append("_");
		sb.append("LNS-ITER_"+lnsIter); 
		return sb.toString();
	}
}

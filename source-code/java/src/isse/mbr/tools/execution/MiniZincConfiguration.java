package isse.mbr.tools.execution;

/**
 * A MiniZinc configuration specifies parameters we want to pass to MiniZinc, e.g., *search for all solutions*.
 * This change is made to reflect the interface changes introduced by MiniZinc 2.2.1
 * 
 * This object represents the users' view (not the internal technicalities regardings flags)
 * @author alexander
 *
 */
public class MiniZincConfiguration {
	private boolean useAllSolutions;
	private Integer stopAfterNSolutions;
	private String solverId;
	private Integer timeout; // in milliseconds
	private Integer randomSeed; // can be null if not specified
	private Integer parallel;
	
	public MiniZincConfiguration() {
		useAllSolutions = true;
		solverId = "Gecode";
		timeout = null;
		randomSeed = null;
		stopAfterNSolutions = null;
		parallel = null;
	}
	/**
	 * @return should we solve for all solutions (-a) or just return the first found solution
	 */
	public boolean isUseAllSolutions() {
		return useAllSolutions;
	}
	
	/**
	 * @param should we solve for all solutions (-a) or just return the first found solution
	 */
	public void setUseAllSolutions(boolean useAllSolutions) {
		this.useAllSolutions = useAllSolutions;
	}
	
	/**
	 * @return the solver's id, must be placed accessible for the present MiniZinc installation (e.g. minizinc --solvers)
	 */
	public String getSolverId() {
		return solverId;
	}
	
	/**
	 * @param the solver's id, must be placed accessible for the present MiniZinc installation (e.g. minizinc --solvers)
	 */
	public void setSolverId(String solverId) {
		this.solverId = solverId;
	}
	
	public Integer getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	public Integer getRandomSeed() {
		return randomSeed;
	}
	public void setRandomSeed(Integer randomSeed) {
		this.randomSeed = randomSeed;
	}
	public Integer getStopAfterNSolutions() {
		return stopAfterNSolutions;
	}
	public void setStopAfterNSolutions(Integer stopAfterNSolutions) {
		this.stopAfterNSolutions = stopAfterNSolutions;
	}
	public Integer getParallel() {
		return parallel;
	}
	public void setParallel(Integer parallel) {
		this.parallel = parallel;
	}
}

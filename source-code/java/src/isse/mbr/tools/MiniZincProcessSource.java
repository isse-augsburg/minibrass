package isse.mbr.tools;

import isse.mbr.tools.execution.MiniZincConfiguration;

/**
 * Is responsible for providing a MiniZinc process according
 * to the solver in use
 * 
 * Reason: Some solvers such as cbc only offer a "mzn_" alternative
 * instead of a separate flatzinc executable
 * @author alexander
 * @deprecated As of MiniZinc 2.2.1, replaced by {@link MiniZincConfiguration}
 *
 */
@Deprecated
public abstract class MiniZincProcessSource {
	protected boolean useAllSolutions;

	public abstract ProcessBuilder getMiniZincProcessBuilder();

	public boolean isUseAllSolutions() {
		return useAllSolutions;
	}

	public void setUseAllSolutions(boolean useAllSolutions) {
		this.useAllSolutions = useAllSolutions;
	}
}

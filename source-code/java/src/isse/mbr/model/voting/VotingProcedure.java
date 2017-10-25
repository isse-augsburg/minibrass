package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.parsing.CodeGenerator;

/**
 * A voting procedure is responsible for generating 
 * code given a set of other PVS 
 * @author Alexander Schiendorfer
 *
 */
public interface VotingProcedure {
	/**
	 * Asks for a "getBetter" predicate in MiniSearch for the respective voting algorithm
	 * @param codeGen
	 * @param votingPvs
	 * @return a string containing a MiniSearch "getBetter" predicate
	 */
	public String getVotingPredicate(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs);
	
	/**
	 * This is relevant if we want a MiniZinc-only version (e.g., counting the number of agents that get their top value) 
	 * @return true if there is a numeric objective
	 */
	public boolean hasNumericObjective(); 
	
	/**
	 * Returns the expression that should be top level objective (not applicable if hasNumericObjective() is false)
	 * @param codeGen
	 * @param votingPvs
	 * @return
	 */
	public String getNumericObjective(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs);
	
	/**
	 * Returns the MiniZinc type of the top level objective (not applicable if hasNumericObjective() is false)
	 * @param codeGen
	 * @param votingPvs
	 * @return
	 */
	public PrimitiveType getObjectiveType(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs);
}

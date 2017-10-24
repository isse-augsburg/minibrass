package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.parsing.CodeGenerator;

/**
 * A voting procedure is responsible for generating 
 * code given a set of other PVS 
 * @author Alexander Schiendorfer
 *
 */
public interface VotingProcedure {
	public String getVotingPredicate(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs);
}

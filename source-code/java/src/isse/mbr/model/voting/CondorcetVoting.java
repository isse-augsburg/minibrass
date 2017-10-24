package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.parsing.CodeGenerator;

/**
 * Implements condorcet voting, i.e., a pairwise majority comparison 
 * @author Alexander Schiendorfer
 *
 */
public class CondorcetVoting implements VotingProcedure {


	@Override
	public String getVotingPredicate(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n(\n");
		boolean first = true; 
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append("+ ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("bool2int(%s)\n", voter.getGeneratedBetterPredicate()));

		}
		sb.append(")\n");
		sb.append("> \n");
		sb.append("\n(\n");
		
		first = true; 
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append("+ ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("bool2int(%s)\n", voter.getGeneratedNotWorsePredicate()));
		}
		sb.append(")\n");
		return sb.toString();
	}

}

package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * Unanimity is agreement by all people in a given situation. Groups may
 * consider unanimous decisions as a sign of agreement, solidarity, and unity.
 * 
 * This requires that no agent opposes the decision to move to another solution - as opposed
 * to Condorcet where only a Majority needs to approve of the next solution
 * @author alexander
 *
 */
public class UnanimityVoting implements VotingProcedure {

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
		sb.append("> 0 /\\ \n"); // At least one agent likes the new solution better
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
		sb.append(") == 0\n"); // nobody likes the old solution strictly better than the new one
		return sb.toString();
	}

	@Override
	public boolean hasNumericObjective() {
		return false;
	}

	@Override
	public String getNumericObjective(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		return null;
	}

	@Override
	public PrimitiveType getObjectiveType(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		return null;
	}

	@Override
	public void sanityCheck(Collection<AbstractPVSInstance> votingPvs) throws MiniBrassParseException {
		// nothing to do for unamity - it is applicable to any PVS 
		
	}

}

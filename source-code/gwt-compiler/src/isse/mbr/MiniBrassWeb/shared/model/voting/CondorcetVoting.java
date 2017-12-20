package isse.mbr.MiniBrassWeb.shared.model.voting;

import java.util.Collection;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.PrimitiveType;
import isse.mbr.MiniBrassWeb.shared.parsing.CodeGenerator;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * Implements condorcet voting, i.e., a pairwise majority comparison
 * 
 * @author Alexander Schiendorfer
 *
 */
public class CondorcetVoting implements VotingProcedure {

	@Override
	public String getVotingPredicate(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n(\n");
		boolean first = true;
		for (AbstractPVSInstance voter : votingPvs) {
			if (first)
				first = false;
			else {
				sb.append("+ ");
			}
			voter = codeGen.deref(voter);
			sb.append("bool2int(" + voter.getGeneratedBetterPredicate() + ")\n");

		}
		sb.append(")\n");
		sb.append("> \n");
		sb.append("\n(\n");

		first = true;
		for (AbstractPVSInstance voter : votingPvs) {
			if (first)
				first = false;
			else {
				sb.append("+ ");
			}
			voter = codeGen.deref(voter);
			sb.append("bool2int(" + voter.getGeneratedNotWorsePredicate() + ")\n");
		}
		sb.append(")\n");
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
		// nothing to do for condorcet - it is applicable to any PVS

	}

}

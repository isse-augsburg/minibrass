package isse.mbr.MiniBrassWeb.shared.model.voting;

import java.util.Collection;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.IntervalType;
import isse.mbr.MiniBrassWeb.shared.model.types.NumericValue;
import isse.mbr.MiniBrassWeb.shared.model.types.PrimitiveType;
import isse.mbr.MiniBrassWeb.shared.parsing.CodeGenerator;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * Tries to have a majority of voters get their top priority
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MajorityTopsVoting implements VotingProcedure {

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
			sb.append("bool2int(" + codeGen.getOverallValuation(voter) + " = " + codeGen.getTopValue(voter) + ")\n");

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
			sb.append(
					"bool2int(sol(" + codeGen.getOverallValuation(voter) + ") = " + codeGen.getTopValue(voter) + ")\n");
		}
		sb.append(")\n");
		return sb.toString();
	}

	@Override
	public boolean hasNumericObjective() {
		return true;
	}

	@Override
	public String getNumericObjective(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
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
			sb.append("bool2int(" + codeGen.getOverallValuation(voter) + " = " + codeGen.getTopValue(voter) + ")\n");

		}
		sb.append(")\n");
		return sb.toString();
	}

	@Override
	public PrimitiveType getObjectiveType(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		return new IntervalType(new NumericValue(0), new NumericValue(votingPvs.size()));
	}

	@Override
	public void sanityCheck(Collection<AbstractPVSInstance> votingPvs) throws MiniBrassParseException {
		// nothing to do, majority tops is applicable for any PVS
	}

}

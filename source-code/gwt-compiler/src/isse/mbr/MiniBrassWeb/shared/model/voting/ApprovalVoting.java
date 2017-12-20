package isse.mbr.MiniBrassWeb.shared.model.voting;

import java.util.Collection;

import isse.mbr.MiniBrassWeb.shared.model.parsetree.AbstractPVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.PVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.ReferencedPVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.BoolType;
import isse.mbr.MiniBrassWeb.shared.model.types.IntervalType;
import isse.mbr.MiniBrassWeb.shared.model.types.MiniZincParType;
import isse.mbr.MiniBrassWeb.shared.model.types.NumericValue;
import isse.mbr.MiniBrassWeb.shared.model.types.PrimitiveType;
import isse.mbr.MiniBrassWeb.shared.parsing.CodeGenerator;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * Implements approval voting (which is only feasible if the supplied PVS are
 * boolean)
 * 
 * @author Alexander Schiendorfer
 *
 */
public class ApprovalVoting implements VotingProcedure {

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
			sb.append("bool2int(" + codeGen.getOverallValuation(voter) + codeGen.getTopValue(voter) + ")\n");

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
			sb.append("bool2int(sol(" + codeGen.getOverallValuation(voter) + codeGen.getTopValue(voter) + "))\n");
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
			sb.append("bool2int(" + codeGen.getOverallValuation(voter) + ")\n");

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
		for (AbstractPVSInstance avps : votingPvs) {
			avps = ReferencedPVSInstance.deref(avps);
			if (avps instanceof PVSInstance) {
				PVSInstance pvs = (PVSInstance) avps;
				MiniZincParType elementType = pvs.getType().instance.getElementType();
				if (!(elementType instanceof BoolType)) {
					throw new MiniBrassParseException(
							"Only boolean atomic PVS supported in an approval voting setting");
				}
			} else
				throw new MiniBrassParseException("Only boolean atomic PVS supported in an approval voting setting");

		}

	}

}

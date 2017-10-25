package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.parsing.CodeGenerator;

/**
 * Tries to have a majority of voters get their top priority 
 * @author Alexander Schiendorfer
 *
 */
public class MajorityTopsVoting implements VotingProcedure {

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
			sb.append(String.format("bool2int(%s = %s)\n", 
					codeGen.getOverallValuation(voter), codeGen.getTopValue(voter)
					));

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
			sb.append(String.format("bool2int(sol(%s) = %s)\n", 
					codeGen.getOverallValuation(voter), codeGen.getTopValue(voter)
					));
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
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append("+ ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("bool2int(%s = %s)\n", 
					codeGen.getOverallValuation(voter), codeGen.getTopValue(voter)
					));

		}
		sb.append(")\n");
		return sb.toString();
	}

	@Override
	public PrimitiveType getObjectiveType(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		return new IntervalType(new NumericValue(0), new NumericValue(votingPvs.size()));
	}

}

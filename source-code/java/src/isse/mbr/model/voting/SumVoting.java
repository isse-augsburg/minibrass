package isse.mbr.model.voting;

import java.util.Collection;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.parsetree.ReferencedPVSInstance;
import isse.mbr.model.types.IntType;
import isse.mbr.model.types.IntervalType;
import isse.mbr.model.types.MiniZincParType;
import isse.mbr.model.types.NumericValue;
import isse.mbr.model.types.PrimitiveType;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * Implements sum voting (which is only feasible if the supplied PVS are integer)
 * Agents may place any weight they like on a decision (which we can then minimize or maximize)
 * @author Alexander Schiendorfer
 *
 */
public class SumVoting implements VotingProcedure {

	protected boolean maximize; // TODO unclear how we set this parameter, maybe keywords sumMax and sumMin
	
	public SumVoting() {
		this(false);
	}
	
	public SumVoting(boolean maximize) {
		this.maximize = maximize;
	}
	
	@Override
	public String getVotingPredicate(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		StringBuilder sb = new StringBuilder();
		sb.append("\nsum([\n");
		boolean first = true; 
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append(", ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("%s", 
					codeGen.getOverallValuation(voter)
					));

		}
		sb.append("])\n");
		
		if(maximize) 
			sb.append("> \n");
		else 
			sb.append("< \n");
		
		sb.append("\nsum([");
		
		first = true; 
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append(", ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("sol(%s)", 
					codeGen.getOverallValuation(voter)
					));
		}
		sb.append("])\n");
		return sb.toString();
	}

	@Override
	public boolean hasNumericObjective() {
		return true;
	}

	@Override
	public String getNumericObjective(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		StringBuilder sb = new StringBuilder();
		sb.append("\nsum([\n");
		boolean first = true; 
		for(AbstractPVSInstance voter : votingPvs) {
			if(first)
				first = false; 
			else {
				sb.append(", ");
			}
			voter = codeGen.deref(voter);
			sb.append(String.format("%s", codeGen.getOverallValuation(voter)));

		}
		sb.append("])\n");
		return sb.toString();
	}

	@Override
	public PrimitiveType getObjectiveType(CodeGenerator codeGen, Collection<AbstractPVSInstance> votingPvs) {
		int max = 0;
		for(AbstractPVSInstance voter : votingPvs) {
			voter = ReferencedPVSInstance.deref(voter);
			PVSInstance pvsInst = (PVSInstance) voter;
			MiniZincParType elementType = pvsInst.getType().instance.getElementType();
			if(elementType instanceof IntType) {
				return (IntType) elementType; // let MiniZinc take control of finding boundaries
			} else if(elementType instanceof IntervalType) {
				IntervalType interval = (IntervalType) elementType;
				// interval could have a parameter, a lookup we leave to MiniZinc for the moment
				if(interval.getUpper().getIntValue() == null)
					return new IntType();
				else 
					max += interval.getUpper().getIntValue();
			}
		}
		
		return new IntervalType(new NumericValue(0), new NumericValue(max));
	}

	@Override
	public void sanityCheck(Collection<AbstractPVSInstance> votingPvs)
			throws MiniBrassParseException {
		for(AbstractPVSInstance avps : votingPvs) {
			avps = ReferencedPVSInstance.deref(avps);
			if(avps instanceof PVSInstance) {
				PVSInstance pvs = (PVSInstance) avps;
				MiniZincParType elementType = pvs.getType().instance.getElementType();
				if (!( (elementType instanceof IntType || elementType instanceof IntervalType))) {
					throw new MiniBrassParseException("Only integer atomic PVS supported in a sum voting setting");					
				}
			} else 
				throw new MiniBrassParseException("Only integer atomic PVS supported in a sum voting setting");
			
		}
		
	}

	public boolean isMaximize() {
		return maximize;
	}

	public void setMaximize(boolean maximize) {
		this.maximize = maximize;
	}

}

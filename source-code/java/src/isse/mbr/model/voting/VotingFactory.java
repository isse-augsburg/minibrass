package isse.mbr.model.voting;

import isse.mbr.parsing.MiniBrassParseException;

/**
 * A factory providing different instantiations of voting procedures
 * can be used for reflection in the future 
 * @author Alexander Schiendorfer
 *
 */
public class VotingFactory {
	public static final String UNANIMITY = "unanimity";
	public static final String SUM_MAX = "sumMax";
	public static final String SUM_MIN = "sumMin";
	public static final String SUM = "sum";
	public static final String APPROVAL = "approval";
	public static final String MAJORITY_TOPS = "majorityTops";
	public static final String CONDORCET = "condorcet";

	public static VotingProcedure getVotingProcedure(String keyword) throws MiniBrassParseException {
		switch(keyword) {
		case CONDORCET:
			return new CondorcetVoting();
		case MAJORITY_TOPS:
			return new MajorityTopsVoting();
		case APPROVAL:
			return new ApprovalVoting();
		case SUM:	
			return new SumVoting();
		case SUM_MIN:	
			return new SumVoting(false);
		case SUM_MAX:	
			return new SumVoting(true);
		case UNANIMITY:
			return new UnanimityVoting();
		default:
				throw new MiniBrassParseException("Voting procedure ["+keyword+"] unknown.");
		}
	
			
	}
}

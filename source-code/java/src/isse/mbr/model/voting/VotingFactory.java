package isse.mbr.model.voting;

import isse.mbr.parsing.MiniBrassParseException;

/**
 * A factory providing different instantiations of voting procedures
 * can be used for reflection in the future 
 * @author Alexander Schiendorfer
 *
 */
public class VotingFactory {
	public static VotingProcedure getVotingProcedure(String keyword) throws MiniBrassParseException {
		if("condorcet".equals(keyword))
			return new CondorcetVoting();
		else 
			throw new MiniBrassParseException("Voting procedure ["+keyword+"] unknown.");
	}
}

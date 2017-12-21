package isse.mbr.model.parsetree;

import java.util.ArrayList;
import java.util.Collection;

import isse.mbr.model.voting.VotingProcedure;

public class VotingInstance extends AbstractPVSInstance {
	protected Collection<AbstractPVSInstance> votingPvs;
	protected VotingProcedure votingProcedure; 
	
	public VotingInstance() {
		votingPvs = new ArrayList<>();
	}
	
	public void addPvs(AbstractPVSInstance pvs) {
		votingPvs.add(pvs);	
	}

	public VotingProcedure getVotingProcedure() {
		return votingProcedure;
	}

	public void setVotingProcedure(VotingProcedure votingProcedure) {
		this.votingProcedure = votingProcedure;
	}

	@Override
	public boolean isComplex() {
		return true;
	}

	@Override
	public Collection<AbstractPVSInstance> getChildren() {
		return votingPvs;
	}

	public void addAllPvs(ArrayList<AbstractPVSInstance> votingPvs2) {
		this.votingPvs.addAll(votingPvs2);
	}
}

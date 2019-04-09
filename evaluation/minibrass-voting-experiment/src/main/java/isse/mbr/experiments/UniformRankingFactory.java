package isse.mbr.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The simplest ranking generation strategy, just a uniform sampling
 * @author alexander
 *
 */
public class UniformRankingFactory implements RankingFactory {
	private List<Integer> companyIds;

	public UniformRankingFactory(List<Integer> companyIds) {
		this.companyIds = companyIds;
	}
	
	/* (non-Javadoc)
	 * @see isse.mbr.experiments.RankingFactory#getRanking(java.util.Random)
	 */
	public List<Integer> getRanking( Random random) {
		Collections.shuffle(companyIds, random);
		return new ArrayList<Integer>(companyIds);
	}
}

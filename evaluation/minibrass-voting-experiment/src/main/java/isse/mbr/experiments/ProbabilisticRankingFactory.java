package isse.mbr.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import isse.mbr.experiments.random.Distribution;

public class ProbabilisticRankingFactory implements RankingFactory {
	private Distribution<Integer> distribution;
	private List<Integer> companyIds;
	
	public ProbabilisticRankingFactory(List<Integer> companyIds, Random random) {
		Map<Integer, Double> probabilitiesForCompany = new HashMap<Integer, Double>();
		int nCompanies = companyIds.size();
		this.companyIds = companyIds;
		double root = Math.sqrt(nCompanies);
		long layers = Math.round(Math.floor(root));
		
		Collections.shuffle(companyIds, random);
		double[] probabilities = new double[nCompanies];

		double sumProb = 0.0;
		
		for(int i = 0; i < nCompanies; ++i) {
			int layer = (int) (i / layers);
			
			probabilities[i] = (double) layer + 1.0;
			sumProb += probabilities[i];
		}

		for(int i = 0; i < nCompanies; ++i) {
			probabilities[i]/=sumProb;
			probabilitiesForCompany.put(i, probabilities[i]);
		}
		distribution = new Distribution<Integer>(probabilitiesForCompany);
	}

	public List<Integer> getRanking(Random random) {
		Distribution<Integer> localDistribution = new Distribution<Integer>(distribution);
		List<Integer> ranking = new ArrayList<Integer>(companyIds.size());
		
		for(int i = 0; i < companyIds.size(); ++i) {
			Integer element = localDistribution.sample(random);
			localDistribution.remove(element);
			ranking.add(element);
		}
		return ranking;
	}

}

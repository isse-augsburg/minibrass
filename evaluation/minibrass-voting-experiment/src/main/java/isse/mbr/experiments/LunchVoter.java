package isse.mbr.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LunchVoter {

	private int studentId;
	private String voterName;
	private List<Integer> privateRanking;
	private int amplifier;
	private int k;
	private int topN; // we want our top n picks to be in selected, the least favorites in deselected
	private int utility;
	
	public LunchVoter(int id, Random random, RankingFactory rankingFactory, int topN) {
		privateRanking = rankingFactory.getRanking(random);
		this.studentId = id;
		voterName = "lunchvoter"+id;
		amplifier = 1; 		
		k = 10000;
		this.topN = topN;
	}

	public List<Integer> getPrivateRanking() {
		return privateRanking;
	}

	public void setPrivateRanking(List<Integer> privateRanking) {
		this.privateRanking = privateRanking;
	}

	public String writePvs(boolean doAmplify) {

		StringBuilder pvsBuilder = new StringBuilder();
		pvsBuilder.append("PVS: ");
		pvsBuilder.append(voterName);
		pvsBuilder.append(" = new WeightedCsp(\"");
		pvsBuilder.append(voterName);
		pvsBuilder.append("\") {\n");
		String targetSet = "selected";
		
		
		for(int j = 0; j < privateRanking.size(); ++j) {
			if(j >= topN)
				targetSet = "deselected";
			
			pvsBuilder.append("\t soft-constraint ");
			
			pvsBuilder.append(voterName + "_rank_"+j);
			pvsBuilder.append(": '");
			pvsBuilder.append(privateRanking.get(j));
			pvsBuilder.append(" in " );
			pvsBuilder.append(targetSet);
			pvsBuilder.append("' :: weights('");
			pvsBuilder.append(privateRanking.size()-j);
			pvsBuilder.append("');\n");
		}
		pvsBuilder.append("\t amplifier: '");
		if(doAmplify)
			pvsBuilder.append(amplifier);
		else
			pvsBuilder.append('1');
		pvsBuilder.append("';\n");
		
		pvsBuilder.append("\t k: '");
		pvsBuilder.append(k);
		pvsBuilder.append("';\n");
		
		pvsBuilder.append("};\n");
		return pvsBuilder.toString();
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}

	public String getStudentName() {
		return voterName;
	}

	public void setStudentName(String studentName) {
		this.voterName = studentName;
	}

	public int getRankForCompany(int company) {
		int rank = privateRanking.indexOf(company);
		return rank+1;
	}

	public void evaluate(Set<Integer> selectionJavaSet, Set<Integer> deselectionJavaSet) {
		utility = 0;
		for(int i = 0; i < privateRanking.size(); ++i) {
			int nextFood = privateRanking.get(i);
			Set<Integer> referenceSet = selectionJavaSet;
			if(i >= topN) { // I *want* that food
				referenceSet = deselectionJavaSet;
			} 
			if(referenceSet.contains(nextFood))
				++utility;
		}		
	}

	public int getUtility() {
		return utility;
	}

	public void setUtility(int utility) {
		this.utility = utility;
	}

}

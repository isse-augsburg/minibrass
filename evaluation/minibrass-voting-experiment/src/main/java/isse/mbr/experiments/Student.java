package isse.mbr.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Student {

	private int studentId;
	private String studentName;
	private List<Integer> privateRanking;
	private int amplifier;
	private int k;
	
	public Student(int id, Random random, RankingFactory rankingFactory) {
		privateRanking = rankingFactory.getRanking(random);
		this.studentId = id;
		studentName = "student"+id;
		amplifier = 1; 		
		k = 10000;
	}

	public List<Integer> getPrivateRanking() {
		return privateRanking;
	}

	public void setPrivateRanking(List<Integer> privateRanking) {
		this.privateRanking = privateRanking;
	}

	public String writePvs(boolean doAmplify) {
		// TODO Auto-generated method stub
		/*
		PVS: grimes = new WeightedCsp("grimes") {
		   soft-constraint grimBuc: 'worksAt[grimes] = buc' :: weights('1');
		   soft-constraint grimApa: 'worksAt[grimes] = apa' :: weights('2');
		   soft-constraint grimRav: 'worksAt[grimes] = rav' :: weights('3');
		};
		*/
		
		StringBuilder pvsBuilder = new StringBuilder();
		pvsBuilder.append("PVS: ");
		pvsBuilder.append(studentName);
		pvsBuilder.append(" = new WeightedCsp(\"");
		pvsBuilder.append(studentName);
		pvsBuilder.append("\") {\n");
		
		for(int j = 0; j < privateRanking.size(); ++j) {
			pvsBuilder.append("\t soft-constraint ");
			
			pvsBuilder.append(studentName + "_rank_"+j);
			pvsBuilder.append(": 'worksAt[");
			pvsBuilder.append(studentId);
			pvsBuilder.append("] = " );
			pvsBuilder.append(privateRanking.get(j));
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
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public int getRankForCompany(int company) {
		int rank = privateRanking.indexOf(company);
		return rank+1;
	}

}

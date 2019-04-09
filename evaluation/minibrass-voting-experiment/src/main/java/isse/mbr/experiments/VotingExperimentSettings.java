package isse.mbr.experiments;

public class VotingExperimentSettings {
	private String socialChoiceFunction;
	private boolean amplify;
	private double amplificationProbability;
	
	public VotingExperimentSettings() {
		super();
		amplificationProbability = 0.25;
	}
	
	public double getAmplificationProbability() {
		return amplificationProbability;
	}
	public void setAmplificationProbability(double amplificationProbability) {
		this.amplificationProbability = amplificationProbability;
	}
	public String getSocialChoiceFunction() {
		return socialChoiceFunction;
	}
	public void setSocialChoiceFunction(String socialChoiceFunction) {
		this.socialChoiceFunction = socialChoiceFunction;
	}
	public boolean isAmplify() {
		return amplify;
	}
	public void setAmplify(boolean amplify) {
		this.amplify = amplify;
	}
	
}

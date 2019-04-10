package isse.mbr.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import isse.mbr.model.voting.VotingFactory;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * This class executes a single problem setup with a number of configurations
 * 
 * @author alexander
 *
 */
public class ComparativeLunchExperiment {

	public static void main(String[] args) throws IOException, MiniBrassParseException {
		
		int numberOfExperiments = 5;
		for(int i = 0; i < numberOfExperiments; ++i) {
			SingleLunchDeselectionExperiment experiment = new SingleLunchDeselectionExperiment(1337+i);
			experiment.setup();

			int numberOfIterations = 1;

			int timeoutInSecs = 60 * 5;
			//timeoutInSecs = 10;
			experiment.setTimeout(timeoutInSecs);
			
			// first the amplifierKey
			String amplifierKey = experiment.getAmplifierKey();
			FileWriter fw = new FileWriter(new File("results/lunch/amplifiers_"+i+".csv"));
			fw.write(amplifierKey);
			fw.close();

			// 1st weighted unbiased
			experiment.getSettings().setAmplify(false);
			experiment.setSocialChoiceFunction(VotingFactory.SUM_MIN);
			experiment.setFileName("results/lunch/weights-unbiased_"+i+".csv");
			experiment.execute(numberOfIterations);

			// 2nd weighted biased
			experiment.getSettings().setAmplify(true);
			experiment.setSocialChoiceFunction(VotingFactory.SUM_MIN);
			experiment.setFileName("results/lunch/weights-biased_"+i+".csv");
			experiment.execute(numberOfIterations);

			// 3rd condorcet biased
			experiment.getSettings().setAmplify(true);
			experiment.setSocialChoiceFunction(VotingFactory.CONDORCET);
			experiment.setFileName("results/lunch/condorcet-biased_"+i+".csv");
			experiment.execute(numberOfIterations);

			// 4th unanimity
			experiment.getSettings().setAmplify(true);
			experiment.setSocialChoiceFunction(VotingFactory.UNANIMITY);
			experiment.setFileName("results/lunch/unanimity_"+i+".csv");
			experiment.execute(numberOfIterations);

		}
		
	}

}

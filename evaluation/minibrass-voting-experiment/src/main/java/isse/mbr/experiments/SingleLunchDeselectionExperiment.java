package isse.mbr.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import isse.mbr.model.types.ArrayType;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;
import isse.mbr.tools.execution.MiniZincConfiguration;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincTensor;
import isse.mbr.tools.execution.MiniZincVariable;

/**
 * This class represents a distinct setting of lunch deselection
 * 
 * @author alexander
 *
 */
public class SingleLunchDeselectionExperiment {
	private int randomSeed;
	private int nLunchVoters;
	private int nFoods;
	private int selectedFoods;
	private int timeout;
	
	private String fileName;
	private String amplifierKey;
	
	private VotingExperimentSettings settings;
	private String socialChoiceFunction;
	private List<LunchVoter> lunchVoters;
	
	private boolean uniformRankings; // just draw food preferences from a uniform random distribution

	public static final String MINIZINC_MODEL = "lunch-deselection.mzn";
	private static File constraintModel;
	
	public SingleLunchDeselectionExperiment(int randomSeed) {
		this.randomSeed = randomSeed;
		
		ClassLoader classLoader = SingleLunchDeselectionExperiment.class.getClassLoader();
		constraintModel = new File(classLoader.getResource(MINIZINC_MODEL).getFile());
		
		// default settings
		nLunchVoters = 12;
		nFoods = 7;
		selectedFoods = 4;
		
		socialChoiceFunction = "sumMin";
		uniformRankings = true;
		
		timeout = 15*60;
		settings = new VotingExperimentSettings();
		settings.setAmplify(true);
	}

	public VotingExperimentSettings getSettings() {
		return settings;
	}

	public void setSettings(VotingExperimentSettings settings) {
		this.settings = settings;
	}

	public String getSocialChoiceFunction() {
		return socialChoiceFunction;
	}

	public void setSocialChoiceFunction(String socialChoiceFunction) {
		this.socialChoiceFunction = socialChoiceFunction;
	}

	public static void main(String[] args) throws IOException, MiniBrassParseException {
		SingleLunchDeselectionExperiment experiment = new SingleLunchDeselectionExperiment(1337);
		experiment.setup();
		
		
		experiment.execute(1);
	}

	public void execute(int noIterations) throws IOException, MiniBrassParseException {
		System.out.println("Executing "+noIterations + " for experiment "+getFileName());
		File minibrassFile = File.createTempFile("mbr_vot", ".mbr");
		File dataFile = File.createTempFile("mbr_dat", ".dzn");
		List<File> dataFiles = Arrays.asList(dataFile);
		
		FileWriter writer = new FileWriter(minibrassFile);
		String generatedMiniBrassCode = this.writeMiniBrassFile();
		System.out.println(generatedMiniBrassCode);
		writer.write(generatedMiniBrassCode);
		writer.close();
		
		writer = new FileWriter(dataFile);
		writer.write(this.writeDznFile());
		writer.close();
		
		MiniBrassRunner runner = new MiniBrassRunner();

		MiniZincConfiguration config = new MiniZincConfiguration();
		//config.setTimeout(timeout);
		config.setSolverId("Chuffed");
		config.setSolverId("OSICBC");
		config.setSolverId("Gecode");
		config.setSolverId("OR-Tools");
		//config.setSolverId("Chuffed");
		config.setStopAfterNSolutions(1);
		//config.setParallel(4);
		runner.setMiniZincConfiguration(config);
		runner.setDebug(false);
		runner.setWriteIntermediateFiles(false);
		runner.getMiniZincRunner().setDoLog(false);
		runner.setTimeoutInSeconds(timeout);
		
		StringBuilder csvExport = new StringBuilder();
		Random r = new Random(randomSeed);
		for(int i = 0; i < noIterations; ++i) {
			runner.setInitialRandomSeed(r.nextInt(randomSeed));
			MiniZincSolution lastSolution = runner.executeBranchAndBound(constraintModel, minibrassFile, dataFiles);
			System.out.println(lastSolution.getRawDznSolution());
			
			// evaluate the result, looking at variable "selectedBool"
			// maps food item i to true or false
			MiniZincVariable selected = lastSolution.getVariable("selected");
			MiniZincVariable deselected = lastSolution.getVariable("deselected");
			
			System.out.println(selected.getType());
			System.out.println(selected.getValue());
			Set<Integer> selectionJavaSet = (Set<Integer>) selected.getValue();
			Set<Integer> deselectionJavaSet = (Set<Integer>) deselected.getValue();
			
			// happyness is qualified as the number of appropriate settings that one gets
			
			List<String> nextLine = new ArrayList<String>(nLunchVoters);
			for(int s = 0; s < nLunchVoters; ++s) {
				lunchVoters.get(s).evaluate(selectionJavaSet, deselectionJavaSet);
				
				System.out.println("LunchVoter "+s + " is that happy: "+lunchVoters.get(s).getUtility());
				
				nextLine.add(Integer.toString(lunchVoters.get(s).getUtility()));
			}
			csvExport.append(String.join(",", nextLine));
			csvExport.append("\n");
		}
		String csv = csvExport.toString();
		System.out.println("Final CSV:");
		System.out.println(csv);
		System.out.println("Amplifiers");
		System.out.println(amplifierKey);
		FileWriter fw = new FileWriter(getFileName());
		fw.write(csv);
		fw.close();
		
	}

	public void setup() {
		Random random = new Random(getRandomSeed());

		// generate students
		List<Integer> foodIds = new ArrayList<Integer>(nFoods);
		for (int i = 0; i < nFoods; ++i)
			foodIds.add(i);

		lunchVoters = new ArrayList<LunchVoter>(nLunchVoters);
		
		RankingFactory rf = null;
		if (uniformRankings) {
			rf = new UniformRankingFactory(foodIds);
		} else  {
			rf = new ProbabilisticRankingFactory(foodIds, random);
		}
			
		List<String> amplifiers = new ArrayList<String>(nLunchVoters);
		for (int i = 0; i < nLunchVoters; ++i) {
			LunchVoter lv = new LunchVoter(i, random, rf, selectedFoods);
			lunchVoters.add(lv);
			
			// amplification is an inherent property of every student but it's a trait that needs not be activated
			double randVal = random.nextDouble();
			if(randVal <= settings.getAmplificationProbability()) {
				lv.setAmplifier(nLunchVoters * nFoods);
			}
			System.out.println("Lunch voter #" + i + ": " + Arrays.toString(lv.getPrivateRanking().toArray()) + " amplifying with "+lv.getAmplifier());
			amplifiers.add(lv.getAmplifier() > 1 ? "1" : "0");
		}
		
		amplifierKey = String.join(",", amplifiers);
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public String writeMiniBrassFile() {
		StringBuilder sb = new StringBuilder("include \"defs.mbr\";\n");

		StringBuilder voteStatement = new StringBuilder("solve vote([");
		List<String> voterNames = new ArrayList<String>(lunchVoters.size());
		for (LunchVoter lv : lunchVoters) {
			voterNames.add(lv.getStudentName());
			sb.append(lv.writePvs(settings.isAmplify()));
			sb.append("\n\n");

		}
		voteStatement.append(String.join(",", voterNames));
		voteStatement.append("], ");
		voteStatement.append(socialChoiceFunction);
		voteStatement.append(");\n");
		sb.append(voteStatement.toString());
		return sb.toString();
	}

	public String writeDznFile() {
		StringBuilder dznBuilder = new StringBuilder();

		dznBuilder.append("f = ");
		dznBuilder.append(nFoods);
		dznBuilder.append(";\n");
		// minPerCompany = 1; maxPerCompany = 3;
		dznBuilder.append("selectedCardinality = ");
		dznBuilder.append(selectedFoods);
		dznBuilder.append(";\n");
		return dznBuilder.toString();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAmplifierKey() {
		return amplifierKey;
	}

	public void setAmplifierKey(String amplifierKey) {
		this.amplifierKey = amplifierKey;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}

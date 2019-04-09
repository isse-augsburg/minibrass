package isse.mbr.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import isse.mbr.model.types.ArrayType;
import isse.mbr.parsing.MiniBrassParseException;
import isse.mbr.tools.execution.MiniBrassRunner;
import isse.mbr.tools.execution.MiniZincConfiguration;
import isse.mbr.tools.execution.MiniZincSolution;
import isse.mbr.tools.execution.MiniZincTensor;
import isse.mbr.tools.execution.MiniZincVariable;

/**
 * This class represents a distinct setting of mentor matching 
 * 
 * @author alexander
 *
 */
public class SingleMentorMatchingExperiment {
	private int randomSeed;
	private int nStudents;
	private int nCompanies;
	private int minPerCompany;
	private int maxPerCompany;
	private int timeout;
	
	private String fileName;
	private String amplifierKey;
	
	private VotingExperimentSettings settings;
	private String socialChoiceFunction;
	private List<Student> students;
	
	private boolean uniformRankings;

	public static final String MINIZINC_MODEL = "student-company-matching-mbr.mzn";
	private static File constraintModel;
	
	public SingleMentorMatchingExperiment(int randomSeed) {
		this.randomSeed = randomSeed;
		
		ClassLoader classLoader = SingleMentorMatchingExperiment.class.getClassLoader();
		constraintModel = new File(classLoader.getResource(MINIZINC_MODEL).getFile());
		
		// default settings
		nStudents = 18;
		nCompanies = 6;
		minPerCompany = 2;
		maxPerCompany = 3;
		
		socialChoiceFunction = "sumMin";
		uniformRankings = false;
		
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
		SingleMentorMatchingExperiment experiment = new SingleMentorMatchingExperiment(1337);
		experiment.setup();
		
		
		experiment.execute(30);
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
		config.setSolverId("Chuffed");
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
			
			// evaluate the result, looking at variable "worksAt"
			// maps student i to their company, worksAt[i]
			MiniZincVariable worksAt = lastSolution.getVariable("worksAt");
			
			System.out.println(worksAt.getType());
			System.out.println(worksAt.getValue());
			
			MiniZincTensor tensor = (MiniZincTensor) worksAt.getValue();
			List<String> nextLine = new ArrayList<String>(nStudents);
			for(int s = 0; s < nStudents; ++s) {
				MiniZincVariable v = tensor.get(s);
				int company = (Integer) v.getValue();
				int rank = students.get(s).getRankForCompany(company);
				System.out.println("Student "+s + " works at company "+company + " and likes this ... " + rank);
				
				nextLine.add(Integer.toString(rank));
			}
			csvExport.append(String.join(",", nextLine));
			csvExport.append("\n");
		}
		String csv = csvExport.toString();
		System.out.println("Final CSV:");
		System.out.println(csv);
		FileWriter fw = new FileWriter(getFileName());
		fw.write(csv);
		fw.close();
	}

	public void setup() {
		Random random = new Random(getRandomSeed());

		// generate students
		List<Integer> companyIds = new ArrayList<Integer>(nCompanies);
		for (int i = 0; i < nCompanies; ++i)
			companyIds.add(i);

		students = new ArrayList<Student>(nStudents);
		
		RankingFactory rf = null;
		if (uniformRankings) {
			rf = new UniformRankingFactory(companyIds);
		} else  {
			rf = new ProbabilisticRankingFactory(companyIds, random);
		}
			
		List<String> amplifiers = new ArrayList<String>(nStudents);
		for (int i = 0; i < nStudents; ++i) {
			Student s = new Student(i, random, rf);
			students.add(s);
			
			// amplification is an inherent property of every student but it's a trait that needs not be activated
			double randVal = random.nextDouble();
			if(randVal <= settings.getAmplificationProbability()) {
				s.setAmplifier(nStudents * nCompanies);
			}
			System.out.println("Student #" + i + ": " + Arrays.toString(s.getPrivateRanking().toArray()) + " amplifying with "+s.getAmplifier());
			amplifiers.add(s.getAmplifier() > 1 ? "1" : "0");
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
		List<String> studentNames = new ArrayList<String>(students.size());
		for (Student s : students) {
			studentNames.add(s.getStudentName());
			sb.append(s.writePvs(settings.isAmplify()));
			sb.append("\n\n");

		}
		voteStatement.append(String.join(",", studentNames));
		voteStatement.append("], ");
		voteStatement.append(socialChoiceFunction);
		voteStatement.append(");\n");
		sb.append(voteStatement.toString());
		return sb.toString();
	}

	public String writeDznFile() {
		StringBuilder dznBuilder = new StringBuilder();
		/**
		 * n = 10; % students m = 5; % companies
		 */
		dznBuilder.append("n = ");
		dznBuilder.append(nStudents);
		dznBuilder.append(";\n");
		dznBuilder.append("m = ");
		dznBuilder.append(nCompanies);
		dznBuilder.append(";\n");
		// minPerCompany = 1; maxPerCompany = 3;
		dznBuilder.append("minPerCompany = ");
		dznBuilder.append(minPerCompany);
		dznBuilder.append(";\n");
		dznBuilder.append("maxPerCompany = ");
		dznBuilder.append(maxPerCompany);
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

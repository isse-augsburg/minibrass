package de.isse.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import de.isse.jobs.JobResult;

public class ReadResults {


	public Collection<JobResult> readValidResults(File resultsDir) {
		Collection<JobResult> validResults = new ArrayList<>(resultsDir.listFiles().length);
		for(File serializedFile : FileUtils.listFiles(resultsDir, new String[]{"ser"}, false)) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(serializedFile);
				ois = new ObjectInputStream(fis);
				JobResult resJob = (JobResult) ois.readObject();
				if(resJob.getResult().valid)
					validResults.add(resJob);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null || ois != null)
					try {
						ois.close();
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return validResults;
	}
	
	public static void main(String[] args) {
		File resultsDir = new File("./results");
		ReadResults rr = new ReadResults();
		Collection<JobResult> results = rr.readValidResults(resultsDir);
		System.out.println("Read "+results.size() + " results");
	}

}

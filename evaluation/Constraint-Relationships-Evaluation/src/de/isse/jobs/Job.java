package de.isse.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import de.isse.conf.MiniBrassConfig;
import de.isse.conf.Solver;

/*
 * Represents one specific evaluation task
 */
public class Job implements Serializable {

	private static final long serialVersionUID = -2529441089155733479L;

	public final Solver solver;

	public final MiniBrassConfig config;

	public final String problem;

	public final String instance;

	public final String problemMD5;

	public final String instanceMD5;

	public Job(File problem2, File instance2, Solver s, MiniBrassConfig mbConfig) {
		solver = s;
		config = mbConfig;
		problem = problem2.getName();
		instance = instance2.getName();
		FileInputStream fis;
		String pMD5 = problem;
		String iMD5 = instance;
		
		try {
			fis = new FileInputStream(new File(problem2, problem+".mzn"));
			pMD5 = DigestUtils.md5Hex(fis);
			fis.close();
			
			fis = new FileInputStream(instance2);
			iMD5 = DigestUtils.md5Hex(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			problemMD5 = pMD5;
			instanceMD5 = iMD5;
		}		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Job))
			return false;
		else {
			Job otherJob = (Job) obj;
			try {
				return solver == otherJob.solver && config.equals(otherJob.config) && problem.equals(otherJob.problem)
						&& instance.equals(otherJob.instance) && instanceMD5.equals(otherJob.instanceMD5)
						&& problemMD5.equals(otherJob.problemMD5);
			} catch (NullPointerException ne) {
				throw new RuntimeException("There should be no null values! ", ne);
			}
		}
	}
	
	@Override
	public int hashCode() {
		return solver.hashCode() + config.hashCode() + problem.hashCode() + instance.hashCode() + problemMD5.hashCode() + instanceMD5.hashCode();
	}
	
	public String toFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(problem); sb.append("_");
		sb.append(FilenameUtils.removeExtension(instance)); sb.append("_");
		sb.append(config.toFileName());
		sb.append("_"); sb.append(solver);
		return FilenameUtils.normalize(sb.toString());
	}
}

package de.isse.jobs;

import java.io.Serializable;

import de.isse.results.MiniBrassResult;

/**
 * A Job-Result pair that is serialized to check for existence
 * of a job to be calculated. 
 * @author Alexander Schiendorfer
 *
 */
public class JobResult implements Serializable {

	private static final long serialVersionUID = -1000663256297282641L;
	
	private Job job;
	private MiniBrassResult result;
	
	@Override
	public int hashCode() {
		return (job == null? 0 : job.hashCode()) + (result == null ? 0 : result.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof JobResult))
			return false;
		else {
			JobResult other = (JobResult) obj;
			try {
				if(job == null && other.job != null)
					return false;
				if(result == null && other.result != null)
					return false;
				
				if(job != null && !job.equals(other.job))
					return false;

				if(result != null && !result.equals(other.result))
					return false;
				
				return true;
				
				} catch (NullPointerException ne) {
				throw new RuntimeException("There should be no unhandled null values! ", ne);
			}
		}
	}
	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public MiniBrassResult getResult() {
		return result;
	}
	public void setResult(MiniBrassResult result) {
		this.result = result;
	}
	
	
}

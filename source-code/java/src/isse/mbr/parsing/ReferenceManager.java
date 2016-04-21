package isse.mbr.parsing;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import isse.mbr.model.types.NamedRef;

/**
 * For deferred reference updates 
 * @author Alexander Schiendorfer
 *
 */
public class ReferenceManager {
	private Queue<ReferenceJob> jobs;
	public ReferenceManager() {
		jobs = new LinkedList<>();
	}
	
	private static class ReferenceJob {
		public NamedRef<?> reference;
		public Map<String, ?> map;
		public ReferenceJob(NamedRef<?> reference, Map<String, ?> map) {
			super();
			this.reference = reference;
			this.map = map;
		}
		
	}
	
	public <T> void scheduleUpdate(NamedRef<T> reference, Map<String, T> map) {
		ReferenceJob newJob = new ReferenceJob(reference, map);
		jobs.add(newJob);
		
	}
	
	public void updateReferences() throws MiniBrassParseException {
		for(ReferenceJob job : jobs) {
			if(job.reference.name != null ) {
				if(!job.map.containsKey(job.reference.name)) {
					throw new MiniBrassParseException("Unresolved reference: "+job.reference.name);
				} else {
					job.reference.update(job.map.get(job.reference.name));
				}
			}
		}
	}
}

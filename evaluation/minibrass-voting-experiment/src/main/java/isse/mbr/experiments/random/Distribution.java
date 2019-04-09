package isse.mbr.experiments.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Distribution<T>{
    List<Double> probs = new ArrayList<Double>();
    List<T> events = new ArrayList<T>();
    double sumProb;

    public Distribution(Map<T,Double> probs){
        for(T event : probs.keySet()){
        	double probForEvent = probs.get(event);
            sumProb += probForEvent;
            events.add(event);
            this.probs.add(probForEvent);
        }
    }

    public Distribution(Distribution<T> distribution) {
		probs = new ArrayList<Double>(distribution.probs);
		events = new ArrayList<T>(distribution.events);
		sumProb = distribution.sumProb;
	}

	public T sample(Random rand){
        double prob = rand.nextDouble()*sumProb;
        int i;
        for(i=0; prob>0; i++){
            prob -= probs.get(i);
        }
        return events.get(i-1);
    }
    
    /**
     * This method is intended for sampling without replacement
     * @param event
     */
    public void remove(T event) {
    	int indexOfEvent = events.indexOf(event);
    	events.remove(indexOfEvent);
    	double removedProb = probs.get(indexOfEvent);
    	probs.remove(indexOfEvent);
    	
    	// we are missing removedProb now from sumProb
    	double delta = removedProb / events.size();
    	
    	// renaturalize the probabilities
    	for(int i = 0; i < probs.size(); ++i)  {
    		probs.set(i, probs.get(i) + delta);
    	}
    }
}
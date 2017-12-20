package isse.mbr.MiniBrassWeb.shared.model.parsetree;

import java.util.Collection;

/**
 * Abstract PVS instance might be a specific (atomic) PVS instance as well
 * as a complex one (composed by product)
 * @author Alexander Schiendorfer
 *
 */
public abstract class AbstractPVSInstance {
	protected String name;
	protected String generatedBetterPredicate;  // strict dominance improvement for BaB
	protected String generatedNotWorsePredicate; // not worsening for BaB and the like but also for voting 
	protected String generatedEqualsPredicate;   // sol(%overall) = %overall, needed for the lex product 
	
	protected Collection<AbstractPVSInstance> children;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGeneratedBetterPredicate() {
		return generatedBetterPredicate;
	}

	public void setGeneratedBetterPredicate(String generatedBetterPredicate) {
		this.generatedBetterPredicate = generatedBetterPredicate;
	}

	public String getGeneratedNotWorsePredicate() {
		return generatedNotWorsePredicate;
	}

	public void setGeneratedNotWorsePredicate(String generatedNotWorsePredicate) {
		this.generatedNotWorsePredicate = generatedNotWorsePredicate;
	}

	public abstract boolean isComplex();

	// this should basically be a read-only property
	public abstract Collection<AbstractPVSInstance> getChildren();

	public String getGeneratedEqualsPredicate() {
		return generatedEqualsPredicate;
	}

	public void setGeneratedEqualsPredicate(String generatedEqualsPredicate) {
		this.generatedEqualsPredicate = generatedEqualsPredicate;
	}

	
}

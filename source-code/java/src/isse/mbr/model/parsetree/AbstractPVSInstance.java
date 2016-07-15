package isse.mbr.model.parsetree;

public abstract class AbstractPVSInstance {
	protected String name;
	protected String generatedBetterPredicate;
	
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
}

package isse.mbr.model.parsetree;

public class ReferencedPVSInstance extends AbstractPVSInstance {
	private String reference;
	private AbstractPVSInstance referencedInstance;
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public AbstractPVSInstance getReferencedInstance() {
		return referencedInstance;
	}
	public void setReferencedInstance(AbstractPVSInstance referencedInstance) {
		this.referencedInstance = referencedInstance;
	}
	
	@Override
	public String toString() {
		return "(ref) -> "+reference;
	}
}

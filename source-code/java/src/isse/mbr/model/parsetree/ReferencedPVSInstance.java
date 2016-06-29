package isse.mbr.model.parsetree;

import isse.mbr.model.types.NamedRef;

public class ReferencedPVSInstance extends AbstractPVSInstance {
	private String reference;
	private NamedRef<AbstractPVSInstance> referencedInstance;
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public NamedRef<AbstractPVSInstance> getReferencedInstance() {
		return referencedInstance;
	}
	public void setReferencedInstance(NamedRef<AbstractPVSInstance> referencedInstance) {
		this.referencedInstance = referencedInstance;
	}
	
	@Override
	public String toString() {
		return "(ref) -> "+reference;
	}
}

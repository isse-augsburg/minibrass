package isse.mbr.model.parsetree;

import java.util.Collection;

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
	public boolean isComplex() {
		return referencedInstance.instance.isComplex();
	}
	
	@Override
	public String toString() {
		return "(ref) -> "+reference;
	}
	@Override
	public Collection<AbstractPVSInstance> getChildren() {
		return referencedInstance.instance.getChildren();
	}
	
	public static AbstractPVSInstance deref(AbstractPVSInstance pvsInstance) {
		if (pvsInstance instanceof ReferencedPVSInstance) {
			ReferencedPVSInstance refPvs = (ReferencedPVSInstance) pvsInstance;
			return ReferencedPVSInstance.deref(refPvs.getReferencedInstance().instance);
		} else
			return pvsInstance;
	}
}

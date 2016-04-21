package isse.mbr.model.types;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Holds all information that is necessary to instantiate 
 * a particular PVS; Examples are constraint relationships,
 * weighted CSP and fuzzy CSP, they all 
 * should be provided as default library elements in MiniBrass
 * @author Alexander Schiendorfer
 *
 */
public class PVSType {
	private MiniZincVarType specType;
	private MiniZincVarType elementType;
	private String name;
	private String combination;
	private String order;
	private String top;
	private Collection<PVSParameter> pvsParameters;
	private String implementationFile; 
	private boolean isBounded; 
	
	public PVSType(MiniZincVarType specType, MiniZincVarType elementType, String name, String combination, String order, String top) {
		super();
		this.setSpecType(specType);
		this.elementType = elementType;
		this.name = name;
		this.combination = combination;
		this.order = order;
		this.top = top;

		this.pvsParameters = new LinkedList<PVSParameter>();
	}

	// empty constructor to be filled by parser
	public PVSType() {
		this.pvsParameters = new LinkedList<PVSParameter>();
	}
	
	@Override
	public String toString() {
		return "PVS-Type: "+name + " <" + elementType.toString() +"("+combination+", "+order+", "+top+ ")"+">";
	}

	public MiniZincVarType getElementType() {
		return elementType;
	}

	public void setElementType(MiniZincVarType elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public MiniZincVarType getSpecType() {
		return specType;
	}

	public void setSpecType(MiniZincVarType specType) {
		this.specType = specType;
	}

	public Collection<PVSParameter> getPvsParameters() {
		return pvsParameters;
	}

	public void setPvsParameters(Collection<PVSParameter> pvsParameters) {
		this.pvsParameters = pvsParameters;
	}

	public String getImplementationFile() {
		return implementationFile;
	}

	public void setImplementationFile(String implementationFile) {
		this.implementationFile = implementationFile;
	}

	public boolean isBounded() {
		return isBounded;
	}

	public void setBounded(boolean isBounded) {
		this.isBounded = isBounded;
	}
}

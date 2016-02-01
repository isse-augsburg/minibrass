package isse.mbr.model.types;

/**
 * Holds all information that is necessary to instantiate 
 * a particular PVS; Examples are constraint relationships,
 * weighted CSP and fuzzy CSP, they all 
 * should be provided as default library elements in MiniBrass
 * @author Alexander Schiendorfer
 *
 */
public class PVSType {
	private MiniZincVarType elementType;
	private String name;
	
	public PVSType(MiniZincVarType elementType, String name) {
		super();
		this.elementType = elementType;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "PVS-Type: "+name + " <" + elementType.toString() +">";
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
}

package isse.mbr.model.types;

/**
 * Represents a single parameter that can be added to a PVSType
 * 
 * @author Alexander Schiendorfer
 *
 */
public class PVSParameter {
	String name;
	MiniZincParType type;
	
	public PVSParameter(String name, MiniZincParType type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MiniZincParType getType() {
		return type;
	}
	public void setType(MiniZincParType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

package isse.mbr.model.types;

/**
 * Represents a single parameter that can be added to a PVSType; 
 * amounts to the *formal* parameter that includes a default value;
 * If the parameter is of an ArrayType, defaultValue denotes the default value
 * for every single item 
 * 
 * @author Alexander Schiendorfer
 *
 */
public class PVSParameter {
	String name;
	MiniZincParType type;
	String defaultValue;
	String wrappedBy;
	
	public PVSParameter(String name, MiniZincParType type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return (name == null ? 0 : name.hashCode()) + (type == null ? 0 : type.hashCode()) + (defaultValue == null ? 0 : defaultValue.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof PVSParameter))
			return false;
		PVSParameter other = (PVSParameter) obj;
		
		return name.equals(other.name) && type.equals(other.type) && ( (defaultValue == null && other.defaultValue == null) || (defaultValue != null && defaultValue.equals(other.defaultValue)));
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

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getWrappedBy() {
		return wrappedBy;
	}

	public void setWrappedBy(String wrappedBy) {
		this.wrappedBy = wrappedBy;
	}
}

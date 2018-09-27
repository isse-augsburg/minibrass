package isse.mbr.tools.execution;

import isse.mbr.model.types.MiniZincParType;

public class MiniZincVariable {
	private MiniZincParType type;
	private Object value;
	private String name;
	private String mznExpression;
	
	public MiniZincVariable(MiniZincParType type, Object value, String name) {
		super();
		this.type = type;
		this.value = value;
		this.name = name;
	}
	
	public MiniZincVariable(String name) {
		this.name = name;
	}

	public MiniZincParType getType() {
		return type;
	}
	public void setType(MiniZincParType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getMznExpression() {
		return mznExpression;
	}

	public void setMznExpression(String mznExpression) {
		this.mznExpression = mznExpression;
	}
}

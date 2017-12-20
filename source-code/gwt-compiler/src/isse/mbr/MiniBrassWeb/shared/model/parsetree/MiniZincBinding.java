package isse.mbr.MiniBrassWeb.shared.model.parsetree;

public class MiniZincBinding {
	private String metaVariable;
	private String minizincVariable;
	public String getMetaVariable() {
		return metaVariable;
	}
	public void setMetaVariable(String metaVariable) {
		this.metaVariable = metaVariable;
	}
	public String getMinizincVariable() {
		return minizincVariable;
	}
	public void setMinizincVariable(String minizincVariable) {
		this.minizincVariable = minizincVariable;
	}
	public MiniZincBinding(String metaVariable, String minizincVariable) {
		super();
		this.metaVariable = metaVariable;
		this.minizincVariable = minizincVariable;
	}
}

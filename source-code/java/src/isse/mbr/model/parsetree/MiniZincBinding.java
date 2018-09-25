package isse.mbr.model.parsetree;

import isse.mbr.model.types.NamedRef;

/**
 * A MiniZinc binding is responsible for associating a meta-variable (number of voters, identifiers of voters) to
 * concrete parameters in a specific constraint model
 * 
 * @author alexander
 *
 */
public class MiniZincBinding {
	private String metaVariable;
	private String minizincVariable;
	private NamedRef<AbstractPVSInstance> scopeInstRef; // the enclosing vote inst

	public String getMetaVariable() {
		return metaVariable;
	}

	public MiniZincBinding(String metaVariable, String minizincVariable, NamedRef<AbstractPVSInstance> scopeInstRef) {
		super();
		this.metaVariable = metaVariable;
		this.minizincVariable = minizincVariable;
		this.scopeInstRef = scopeInstRef;
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

	public NamedRef<AbstractPVSInstance> getScopeInstRef() {
		return scopeInstRef;
	}

	public void setScopeInstRef(NamedRef<AbstractPVSInstance> scopeInstRef) {
		this.scopeInstRef = scopeInstRef;
	}
}

package isse.mbr.model.types;

public class PVSParamInst {
	public PVSParameter parameter;
	public String expression;
	public boolean generated = false;
	public boolean isGenerated() {
		return generated;
	}
	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
}

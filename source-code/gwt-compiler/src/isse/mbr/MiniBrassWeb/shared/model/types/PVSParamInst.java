package isse.mbr.MiniBrassWeb.shared.model.types;

public class PVSParamInst {
	public PVSFormalParameter parameter;
	public String expression;
	public boolean generated = false;
	public boolean isGenerated() {
		return generated;
	}
	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
}

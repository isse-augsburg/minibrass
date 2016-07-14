package isse.mbr.model.parsetree;

public class CompositePVSInstance extends AbstractPVSInstance {
	private ProductType productType;
	private AbstractPVSInstance leftHandSide;
	private AbstractPVSInstance rightHandSide;
	
	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public AbstractPVSInstance getLeftHandSide() {
		return leftHandSide;
	}

	public void setLeftHandSide(AbstractPVSInstance leftHandSide) {
		this.leftHandSide = leftHandSide;
	}

	public AbstractPVSInstance getRightHandSide() {
		return rightHandSide;
	}

	public void setRightHandSide(AbstractPVSInstance rightHandSide) {
		this.rightHandSide = rightHandSide;
	} 
	
	@Override
	public String toString() {
		return "( " + leftHandSide.toString() + " " + (productType == ProductType.LEXICOGRAPHIC ? "lex" : "*") + " " + rightHandSide.toString() + " )";
	}
	
}

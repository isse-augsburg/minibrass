package isse.mbr.model.parsetree;

import java.util.Arrays;
import java.util.Collection;

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
		return String.format("( %s %s %s )", leftHandSide.toString(),
				productType == ProductType.LEXICOGRAPHIC ? "lex" : productType == ProductType.DIRECT ? "direct" : "pareto",
				rightHandSide.toString());
	}

	@Override
	public boolean isComplex() {
		return true;
	}

	@Override
	public Collection<AbstractPVSInstance> getChildren() {
		return Arrays.asList(leftHandSide, rightHandSide);
	}

}

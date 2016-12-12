package isse.mbr.parsing;

import isse.mbr.extensions.ExternalParameterWrap;

/**
 * Contains all information of a wrappedBy annotation 
 * during preprocessing of parameter instantiations
 * @author Alexander Schiendorfer
 *
 */
public class WrapInformation {
	
	public final static String MINIZINC = "minizinc";
	public final static String JAVA = "java";
	
	public String wrapFunction;
	public String wrapLanguage;
	private ExternalParameterWrap externalWrap;
	
	@Override
	public String toString() {
		throw new RuntimeException("This cannot be cast to String!");
	}

	public void setExternalWrap(ExternalParameterWrap epw) {
		this.externalWrap = epw;
	}

	public ExternalParameterWrap getExternalWrap() {
		return externalWrap;
	}
}

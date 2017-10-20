package isse.mbr.model.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import isse.mbr.parsing.MiniBrassParseException;

/**
 * Holds all information that is necessary to instantiate 
 * a particular PVS; Examples are constraint relationships,
 * weighted CSP and fuzzy CSP, they all 
 * should be provided as default library elements in MiniBrass
 * @author Alexander Schiendorfer
 *
 */
public class PVSType {
	private MiniZincParType specType;
	private MiniZincParType elementType; // this can be an array as well
	private String name;
	private String combination;
	private String order;
	private String top;
	private List<PVSParameter> pvsParameters;
	private Map<String, PVSParameter> paramMap;
	private String implementationFile; 
	private String orderingHeuristic;
	private NamedRef<PVSType> representsType;
	
	private boolean isBounded; 
	
	public final static String N_SCS_LIT = "nScs";

	// empty constructor to be filled by parser
	public PVSType() {
		this.pvsParameters = new LinkedList<PVSParameter>();
		this.paramMap = new HashMap<>();

		PVSParameter nScsParam = new PVSParameter(N_SCS_LIT, new IntType());
		this.pvsParameters.add(nScsParam);		
		paramMap.put(N_SCS_LIT, nScsParam);
	}
	
	@Override
	public String toString() {
		return "PVS-Type: "+name + " <" + elementType.toString() +"("+combination+", "+order+", "+top+ ")"+">";
	}

	public MiniZincParType getElementType() {
		return elementType;
	}

	public void setElementType(MiniZincParType elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public MiniZincParType getSpecType() {
		return specType;
	}

	public void setSpecType(MiniZincParType specType) {
		this.specType = specType;
	}

	public List<PVSParameter> getPvsParameters() {
		return pvsParameters;
	}

	public void setPvsParameters(List<PVSParameter> pvsParameters) {
		this.pvsParameters = pvsParameters;
	}

	public String getImplementationFile() {
		return implementationFile;
	}

	public void setImplementationFile(String implementationFile) {
		this.implementationFile = implementationFile;
	}

	public boolean isBounded() {
		return isBounded;
	}

	public void setBounded(boolean isBounded) {
		this.isBounded = isBounded;
	}

	public Map<String, PVSParameter> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, PVSParameter> paramMap) {
		this.paramMap = paramMap;
	}

	public void addPvsParameter(PVSParameter par) throws MiniBrassParseException {
		if(paramMap.containsKey(par.getName()) )
			throw new MiniBrassParseException("Type "+name + " already contains a parameter: "+par.getName());
		else {
			pvsParameters.add(par);
			paramMap.put(par.getName(), par);
		}
	}

	public String getOrderingHeuristic() {
		return orderingHeuristic;
	}

	public void setOrderingHeuristic(String orderingHeuristic) {
		this.orderingHeuristic = orderingHeuristic;
	}

	public NamedRef<PVSType> getRepresentsType() {
		return representsType;
	}

	public void setRepresentsType(NamedRef<PVSType> representsType) {
		this.representsType = representsType;
	}
}

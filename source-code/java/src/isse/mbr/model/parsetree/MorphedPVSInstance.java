package isse.mbr.model.parsetree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import isse.mbr.model.parsetree.Morphism.ParamMapping;
import isse.mbr.model.types.NamedRef;
import isse.mbr.model.types.PVSParamInst;
import isse.mbr.model.types.PVSParameter;
import isse.mbr.model.types.PVSType;

/**
 * This is a PVS instance that has a morphism applied
 * The morphism must match to the input type and returns
 * an instance of the output type
 * @author Alexander Schiendorfer
 *
 */
public class MorphedPVSInstance extends PVSInstance {
	private AbstractPVSInstance input;
	private NamedRef<Morphism> morphism;
	private PVSInstance concreteInstance;
	private List<PVSParameter> joinedPars;
	
	public void deref() {
		AbstractPVSInstance inst = input;
		while(inst instanceof ReferencedPVSInstance) {
			inst = ( (ReferencedPVSInstance) inst ).getReferencedInstance().instance; 
		}
		concreteInstance = (PVSInstance) inst;
	}
	
	@Override
	public NamedRef<PVSType> getType() {
		// it should be considered a PVS of type "to" of the enclosing morphism
		return morphism.instance.getTo();
	}
	
	@Override
	public Map<String, String> getParameterValues() {
		// for now just the decorated instance's parameter values;
		// the dependent ones added by the morphism are added in the codegenerator
		return concreteInstance.getParameterValues();
	}
	
	@Override
	public int getNumberSoftConstraints() {
		return concreteInstance.getNumberSoftConstraints();
	}
	
	@Override
	public Map<String, SoftConstraint> getSoftConstraints() {
		return concreteInstance.getSoftConstraints();
	}
	
	@Override
	public Map<String, PVSParamInst> getParametersInstantiated() {
		return this.parametersInstantiated;
	}
	
	public AbstractPVSInstance getInput() {
		return input;
	}

	public void setInput(AbstractPVSInstance input) {
		this.input = input;
	}

	public NamedRef<Morphism> getMorphism() {
		return morphism;
	}

	public void setMorphism(NamedRef<Morphism> morphism) {
		this.morphism = morphism;
	}

	public PVSInstance getConcreteInstance() {
		return concreteInstance;
	}

	/**
	 * For a morphed PVS instance, this includes the "from" and "to" parameters!
	 */
	@Override
	public List<PVSParameter> getInstanceParameters() {
		return joinedPars;
	}
	
	/**
	 * @param fromArguments
	 */
	public void update(StringBuilder fromArguments) {
		Map<String, PVSParamInst> parInst = new LinkedHashMap<>();
		parInst.putAll(concreteInstance.getParametersInstantiated());
		
		// now put instantiations for all the morphed result parameters (those of type "to")
		PVSType toType = morphism.instance.getTo().instance;
		
		for(ParamMapping parMapping : morphism.instance.getParamMappings().values()) {
			PVSParameter par = toType.getParamMap().get(parMapping.getParam());
			PVSParamInst pi = new PVSParamInst();
			pi.parameter = par; 
			if(parMapping.getMznExpression()!=null){
				pi.expression = parMapping.getMznExpression();
			} else {
				pi.expression = parMapping.getMznFunction()+"("+fromArguments.toString()+")";
			}
			parInst.put(par.getName(), pi);
		}
		this.parametersInstantiated = parInst;
		

		List<PVSParameter> fromPars = morphism.instance.getFrom().instance.getPvsParameters();
		List<PVSParameter> toPars = morphism.instance.getTo().instance.getPvsParameters();
		
		joinedPars = new ArrayList<>(fromPars.size()+toPars.size());
		joinedPars.addAll(fromPars);
		for(PVSParameter toPar : toPars) {
			if(! toPar.getName().equals(PVSType.N_SCS_LIT)) {
				joinedPars.add(toPar);
			}
		}
	}
}

package isse.mbr.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import isse.mbr.model.parsetree.AbstractPVSInstance;
import isse.mbr.model.parsetree.MiniZincBinding;
import isse.mbr.model.parsetree.Morphism;
import isse.mbr.model.types.PVSType;
import isse.mbr.parsing.MiniBrassParser;

/**
 * A full soft constraint model in MiniBrass
 * that consists of PVS types, instantiations of
 * PVS, their algebraic combinations and a main PVS
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniBrassAST {

	private final static Logger LOGGER = Logger.getGlobal();
	
	private Map<String, PVSType> pvsTypes;
	private Map<String, Morphism> morphisms;
	private Map<String, AbstractPVSInstance> pvsInstances; // here by identifier
	private Map<String, AbstractPVSInstance> pvsReferences; // here by reference
	private Collection<String> additionalMinizincIncludes;
	private Collection<MiniZincBinding> bindings; // to map voting meta-variables to MiniZinc variables
	
	private AbstractPVSInstance solveInstance;

	private String problemOutput; 
	
	public MiniBrassAST() {
		pvsTypes = new HashMap<String, PVSType>();
		pvsInstances = new HashMap<>();
		pvsReferences =  new HashMap<>();
		morphisms = new HashMap<>();
		additionalMinizincIncludes = new LinkedList<>();
		bindings = new LinkedList<>();
	}
	
	public void registerBinding(MiniZincBinding binding	) {
		bindings.add(binding);
	}
	public void registerPVSType(String reference, PVSType type) {
		LOGGER.fine("Registering PVS type ... "+ type);
		pvsTypes.put(reference, type);
	}

	public AbstractPVSInstance getSolveInstance() {
		return solveInstance;
	}

	public void setSolveInstance(AbstractPVSInstance solveInstance) {
		this.solveInstance = solveInstance;
		this.pvsReferences.put(MiniBrassParser.TOP_LEVEL_PVS_REF, solveInstance);
	}

	public Map<String, AbstractPVSInstance> getPvsInstances() {
		return pvsInstances;
	}

	public void setPvsInstances(Map<String, AbstractPVSInstance> pvsInstances) {
		this.pvsInstances = pvsInstances;
	}

	public Map<String, AbstractPVSInstance> getPvsReferences() {
		return pvsReferences;
	}

	public void setPvsReferences(Map<String, AbstractPVSInstance> pvsReferences) {
		this.pvsReferences = pvsReferences;
	}

	public Map<String, PVSType> getPvsTypes() {
		return pvsTypes;
	}

	public void setPvsTypes(Map<String, PVSType> pvsTypes) {
		this.pvsTypes = pvsTypes;
	}

	public void registerMorphism(String name, Morphism m) {
		this.morphisms.put(name, m);
	}

	public Map<String, Morphism> getMorphisms() {
		return morphisms;
	}

	public Collection<String> getAdditionalMinizincIncludes() {
		return additionalMinizincIncludes;
	}

	public String getProblemOutput() {
		return problemOutput;
	}

	public void setProblemOutput(String problemOutput) {
		this.problemOutput = problemOutput;
	}

	public Collection<MiniZincBinding> getBindings() {
		return bindings;
	}
}


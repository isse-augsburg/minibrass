package isse.mbr.MiniBrassWeb.shared.extensions;

import java.util.HashMap;
import java.util.Map;

import isse.mbr.MiniBrassWeb.shared.extensions.weighting.MultiWeighting;
import isse.mbr.MiniBrassWeb.shared.extensions.weighting.ProbWeighting;
import isse.mbr.MiniBrassWeb.shared.extensions.weighting.SingleWeighting;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.PVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSFormalParameter;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * This interface defines methods that external morphism objects need to offer;
 * process() means calculating the necessary parameters for the "to" PVS and
 * getParameterString() requires a PVS param and returns a MiniZinc String
 * 
 * @author Alexander Schiendorfer
 *
 */
public abstract class ExternalMorphism {

	private boolean updated = false;
	protected PVSInstance pvsInst;
	protected Map<String, String> calculatedParameters;

	public ExternalMorphism() {
		calculatedParameters = new HashMap<>();
	}

	/**
	 * Called when a morphism is applied, intended to build up data structures that
	 * are then queried by "getParameterString()"
	 * 
	 * @param fromInstance
	 * @throws MiniBrassParseException
	 */
	public abstract void process(PVSInstance fromInstance) throws MiniBrassParseException;

	/**
	 * Called during code generation when a parameter for the "to" PVS is
	 * instantiated
	 * 
	 * @param key
	 * @return
	 * @throws MiniBrassParseException
	 */
	public synchronized String getParameterString(PVSFormalParameter key) throws MiniBrassParseException {
		if (!updated)
			process(pvsInst);
		return calculatedParameters.get(key.getName());
	}

	public void setFromInstance(PVSInstance inst) {
		this.pvsInst = inst;
	}

	public static ExternalMorphism create(String type) {
		if ("isse.mbr.extensions.weighting.MultiWeighting".equals(type))
			return new MultiWeighting();
		if ("isse.mbr.extensions.weighting.ProbWeighting".equals(type))
			return new ProbWeighting();
		if ("isse.mbr.extensions.weighting.SingleWeighting".equals(type))
			return new SingleWeighting();
		return null;
	}

}

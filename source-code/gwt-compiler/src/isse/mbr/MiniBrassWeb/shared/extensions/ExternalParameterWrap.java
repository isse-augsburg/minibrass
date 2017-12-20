package isse.mbr.MiniBrassWeb.shared.extensions;

import isse.mbr.MiniBrassWeb.shared.extensions.preprocessing.TransitiveClosure;
import isse.mbr.MiniBrassWeb.shared.model.parsetree.PVSInstance;
import isse.mbr.MiniBrassWeb.shared.model.types.PVSFormalParameter;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * This class is intended for preprocessing steps of parameters of a PVS
 * instance, for example calculating a transitive closure
 * 
 * @author Alexander Schiendorfer
 *
 */
public abstract class ExternalParameterWrap {
	public abstract String process(PVSInstance inst, PVSFormalParameter pvsParam, String parameter)
			throws MiniBrassParseException;

	public static ExternalParameterWrap create(String type) {
		if ("isse.mbr.extensions.preprocessing.TransitiveClosure".equals(type))
			return new TransitiveClosure();
		return null;
	}
}

package isse.mbr.extensions;

import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.types.PVSFormalParameter;
import isse.mbr.parsing.MiniBrassParseException;

/**
 * This class is intended for preprocessing steps of parameters
 * of a PVS instance, for example calculating a transitive closure
 * @author Alexander Schiendorfer
 *
 */
public abstract class ExternalParameterWrap {
	public abstract String process(PVSInstance inst, PVSFormalParameter pvsParam, String parameter) throws MiniBrassParseException;
}

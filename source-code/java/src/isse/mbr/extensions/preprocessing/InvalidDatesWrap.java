package isse.mbr.extensions.preprocessing;

import isse.mbr.extensions.ExternalParameterWrap;
import isse.mbr.model.parsetree.PVSInstance;
import isse.mbr.model.types.PVSFormalParameter;
import isse.mbr.parsing.CodeGenerator;
import isse.mbr.parsing.MiniBrassParseException;

public class InvalidDatesWrap extends ExternalParameterWrap {

	@Override
	public String process(PVSInstance inst, PVSFormalParameter pvsParam, String parameter)
			throws MiniBrassParseException {
		// TODO This should be made much more generic / i.e., a wrapper that is independent of the used constraint model 
		String constraint = String.format("constraint not ( scheduled[%s] in %s )\n", CodeGenerator.toValidMznIdent(inst.getName()), CodeGenerator.encodeIdent(pvsParam, inst));
		return parameter + ";\n" + constraint;
	}

}

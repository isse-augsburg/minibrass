package isse.mbr.parsing;

import isse.mbr.model.MiniBrassAST;
import isse.mbr.parsing.CodeGenerator.AtomicPvsInformation;

/**
 * Handles atomic PVS and adds code as needed
 * @author Alexander Schiendorfer
 *
 */
public interface CodegenAtomicPVSHandler {
	public void handleAtomicPvs(AtomicPvsInformation api, MiniBrassAST model, StringBuilder sb);
}

package isse.mbr.MiniBrassWeb.shared.parsing;

import isse.mbr.MiniBrassWeb.shared.model.MiniBrassAST;
import isse.mbr.MiniBrassWeb.shared.parsing.CodeGenerator.AtomicPvsInformation;

/**
 * Handles atomic PVS and adds code as needed
 * @author Alexander Schiendorfer
 *
 */
public interface CodegenAtomicPVSHandler {
	public void handleAtomicPvs(AtomicPvsInformation api, MiniBrassAST model, StringBuilder sb);
}

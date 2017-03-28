package isse.mbr.parsing;

/**
 * This class holds literals that can be accessed 
 * from a MiniZinc constraint model with 
 * a MiniBrass preference model
 * 
 * @author Alexander Schiendorfer
 *
 */
public class MiniZincKeywords {

	/**
	 * name of generated search heuristic that can be used in a model:
	 * 
	 * ann: pvsSearchHeuristic = ...
	 * ---
	 * 
	 * used as: 
	 * <pre>
	 * solve 
	 * :: pvsSearchHeuristic 
	 * search pvs_BAB();
	 * </pre>
	 * 
	 */
	public static final String PVS_SEARCH_HEURISTIC = "pvsSearchHeuristic";
	

	/**
	 * the name of the generated topLevelObjective (in case it is to be minimized by MiniZinc or used in the output)
	 * otherwise this may be some cryptic identifier that is influenced by morphisms.
	 * 
	 * var E: topLevelObjective = ...
	 * ---
	 * 
	 * used as: 
	 * <pre>
	 * output ["x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations: topLevelObjective = \(topLevelObjective)\n"];
	 * </pre>
	 */
	public static final String TOP_LEVEL_OBJECTIVE = "topLevelObjective";
	
	/**
	 * the name of the overall top level "get better" predicate that the PVS-based searches 
	 * in "soft_constraints/pvs_gen_search.mzn" expect
	 * 
	 * ann: postGetBetter = ...
	 * --
	 * 
	 * defined in "soft_constraints/pvs_gen_search.mzn"
	 * --------------------------------------------------------------------------------------------------
	 * | % only declare minisearch function that posts a constraint which solution has to surpassed
	 * | function ann: postGetBetter(); % = post(getBetter2(sol(x)));
	 * | 
	 * function ann: pvs_BAB() =
	 * repeat(
	 *     if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
	 *        print("Intermediate solution:") /\ print() /\
	 *        commit() /\ postGetBetter()
	 *        else break endif
	 *     );
     * --------------------------------------------------------------------------------------------------  
	 */
	public static final String POST_GET_BETTER = "postGetBetter";

}

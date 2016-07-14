% instantiating isWorse-predicate
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";
include "soft_constraints/tpd_worse.mzn";

include "soft_constraints/weighting_tpd.mzn";
include "soft_constraints/pvs_set_based.mzn";


predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  tpd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges) 
);

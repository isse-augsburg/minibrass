% instantiating isWorse-predicate
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/pvs_set_based.mzn";

include "soft_constraints/weighting_spd.mzn";

predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  spd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges)
);

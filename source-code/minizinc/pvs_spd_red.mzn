% instantiating isWorse-predicate (including redundant constraint for weights!)
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/pvs_set_based.mzn";
include "soft_constraints/weighting_spd.mzn";

% left should be assumed to be used as parameter from previous solution
% there has to be a boolean parameter propagateWeights that switches whether or not to use redundant propagation
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  spd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges) /\
  if propagateWeights then sum(sc in SOFTCONSTRAINTS) (bool2int(sc in leftViolatedScs) * penalties[sc]) > sum(sc in SOFTCONSTRAINTS) (bool2int(sc in rightViolatedScs) * penalties[sc]) else true endif
);

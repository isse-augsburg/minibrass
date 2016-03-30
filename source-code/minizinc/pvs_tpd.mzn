% instantiating isWorse-predicate
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";

% ground penalties using the appropriate weighting
penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, false) | s in SOFTCONSTRAINTS];
 
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  tpd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges)
);

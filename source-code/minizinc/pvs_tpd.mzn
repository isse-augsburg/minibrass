% instantiating isWorse-predicate
include "cr_types.mzn";
include "cr_weighting.mzn";

% ground penalties using the appropriate weighting
penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, false) | s in SOFTCONSTRAINTS];
 
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  tpd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges)
);

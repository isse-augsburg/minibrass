% instantiating isWorse-predicate
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  tpd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges)
);
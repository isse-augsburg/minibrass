% instantiating isWorse-predicate
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
(
  spd_worse(leftViolatedScs, rightViolatedScs, SOFTCONSTRAINTS, crEdges)
);
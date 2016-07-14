% instantiating isWorse-predicate with weighted csp cases
include "soft_constraints/pvs_set_based.mzn";

% an aggregator function for violations has to be defined (e.g., sum,max,min)
function var int: weightedAggregator(var set of int: leftViolatedScs); 


predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs) = 
let {
  var int: leftObjective = weightedAggregator(leftViolatedScs);
  var int: rightObjective = weightedAggregator(rightViolatedScs);
}
in 
(
  leftObjective > rightObjective  % left is worse if it gets more penalty
);

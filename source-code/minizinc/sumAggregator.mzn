% sum aggregator function for penalties
function var int: weightedAggregator(var set of int: leftViolatedScs) 
:: promise_total = 
(
  sum(sc in SOFTCONSTRAINTS) (bool2int(sc in leftViolatedScs) * penalties[sc] )
);

predicate weightedAggregation(var set of int: leftViolatedScs, var int: solutionDegree) 
= 
(
  solutionDegree = max(sc in SOFTCONSTRAINTS) (bool2int(sc in leftViolatedScs) * penalties[sc] )
);
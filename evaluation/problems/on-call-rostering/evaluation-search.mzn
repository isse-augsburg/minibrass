include "soft_constraints/pvs_search.mzn";

int: exploreTime = 5000;

solve 
:: finalAnn
%search lns_pvs(violatedScs, lnsVars, 20, 0.1, exploreTime) /\ print();
%search strictlyBetterBAB(violatedScs) /\ print();

search adaptive_lns_pvs(violatedScs, lnsVars, 30, 30, exploreTime) /\ print();

%function ann: adaptive_lns_pvs(var set of SOFTCONSTRAINTS:, array[int] of var int: vars,
%                        int: iterations, int: initRate, int: exploreTime) =

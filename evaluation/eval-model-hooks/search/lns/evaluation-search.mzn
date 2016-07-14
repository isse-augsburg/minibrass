include "soft_constraints/pvs_search.mzn";

% timeLimitMs
int: exploreTime = 5000;
int: maxIterations = timeLimitMs div exploreTime;

solve 
:: finalAnn
%search time_limit(timeLimitMs, lns_pvs(violatedScs, lnsVars, maxIterations, 0.1, exploreTime)) /\ print();
%search time_limit(timeLimitMs, strictlyBetterBAB(violatedScs)) /\ print(); 

search time_limit(timeLimitMs, adaptive_lns_pvs(violatedScs, lnsVars, maxIterations, 30, exploreTime)) /\ print();

                
%function ann: adaptive_lns_pvs(var set of SOFTCONSTRAINTS:, array[int] of var int: vars,
%                        int: iterations, int: initRate, int: exploreTime) =

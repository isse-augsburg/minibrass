% Illustrates how partial orders have 
% to be handled differently in propagating 
% lower bounds.

% To play with the script, switch between
% onlyNotDominated or strictly better BAB

% solution degrees are explored in order {3}, {2}, {1}
% constraint relationship states 1 <- 2, 1 <- 3
%   with strictly better, we only get to see {3}
%   with only non-domination, we get both {2} and {3}
% -------------------------------------------

include "minisearch.mzn"; % include the search minisearch lite
include "soft_constraints/pvs_search.mzn"; % search strategies for SPD better
include "soft_constraints/soft_constraints.mzn"; % model additions for soft constraint business
include "soft_constraints/spd_worse.mzn"; % the actual isBetter predicate
include "soft_constraints/tpd_worse.mzn"; % the actual isBetter predicate

% using TPD semantics (SPD requires free variables for the witness -> so no negation)
include "soft_constraints/pvs_tpd.mzn";

% soft constraint specific for this model
nScs = 3;

% ==============================================================
% Now the actual model begins -> add soft constraints
var 1..3: x; 

constraint x = 1 <-> violated[1];
constraint x = 2 <-> violated[2];
constraint x = 3 <-> violated[3];

% our "classical" constraint relationship
nCrEdges = 2;
crEdges = [| 2, 1 | 3, 1 |];

solve 
:: int_search([x], input_order, indomain_max, complete)
search strictlyBetterBAB(violatedScs);% /\ if hasSol() then print("Final solution: ") /\ print() else print("No solution found\n") endif;
%search onlyNotDominatedBAB(violatedScs) /\ if hasSol() then print("Final solution: ") /\ print() else print("No solution found\n") endif;
%satisfy;

output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x)"];

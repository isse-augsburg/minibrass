% A really simplistic over-constrained model
% X: {x,y,z} D_i = {1,2,3}, i in X
%    * c1: x + 1 = y 
%    * c2: z = y + 2
%    * c3: x + y <= 3
% -------------------------------------------

include "link_set_to_booleans.mzn";
include "minisearch.mzn"; % include the search minisearch lite
include "soft_constraints.mzn"; % model additions for soft constraint business
include "spd_better.mzn"; % the actual isBetter predicate
include "constraint_relationship_search.mzn"; % search strategies for SPD better
include "concrete-pvs.mzn";

% soft constraint specific for this model
nScs = 3;

% ==============================================================
% Now the actual model begins -> add soft constraints
var 1..3: x; 
var 1..3: y;
var 1..3: z;

constraint x + 1 = y <-> satisfied[1];
constraint z = y + 2 <-> satisfied[2];
constraint x + y <= 3 <-> satisfied[3];

solve 
:: int_search(satisfied, input_order, indomain_max, complete)
%:: int_search([penC1,penC2,penC3, x, y, z], input_order, indomain_min, complete)
%minimize penSum;
% to find only strictly better solutions (perhaps more pruning)
search time_limit(3000, strictlyBetterBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;
% to find all non-dominated solutions (less pruning, more optima)
%search time_limit(3000, onlyNotDominated(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;


output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x), y  -> \(y), z -> \(z)"];

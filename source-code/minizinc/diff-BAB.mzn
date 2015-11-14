% A really simplistic over-constrained model
% X: {x,y,z} D_i = {1,2,3}, i in X
%    * c1: x + 1 = y 
%    * c2: z = y + 2
%    * c3: x + y <= 3
% -------------------------------------------

include "link_set_to_booleans.mzn";
include "minisearch.mzn"; % include the search minisearch lite
include "constraint_relationship_search.mzn"; % search strategies for SPD better
include "soft_constraints.mzn"; % model additions for soft constraint business
include "spd_better.mzn"; % the actual isBetter predicate
include "tpd_better.mzn"; % the actual isBetter predicate
include "concrete-pvs.mzn";

% soft constraint specific for this model
nScs = 3;

% ==============================================================
% Now the actual model begins -> add soft constraints
var 1..3: x; 

constraint x = 1 <-> violated[1];
constraint x = 2 <-> violated[2];
constraint x = 3 <-> violated[3];

%constraint spd_better(2..3, {1,3}, SOFTCONSTRAINTS, edges);

solve 
:: int_search([x], input_order, indomain_max, complete)
%search time_limit(3000, strictlyBetterBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;
search strictlyBetterBAB_TPD(violatedScs);% /\ if hasSol() then print("Final solution: ") /\ print() else print("No solution found\n") endif;
% search onlyNotDominated_TPD(violatedScs) /\ if hasSol() then print("Final solution: ") /\ print() else print("No solution found\n") endif;
%satisfy;

output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x)"];

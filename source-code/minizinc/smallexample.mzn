% A really simplistic over-constrained model
% X: {x,y,z} D_i = {1,2,3}, i in X
%    * c1: x + 1 = y 
%    * c2: z = y + 2
%    * c3: x + y <= 3
% -------------------------------------------

include "minisearch.mzn"; % include the search minisearch lite
include "soft_constraints.mzn"; % model additions for soft constraint business
include "spd_worse.mzn"; % the actual isBetter predicate
include "tpd_worse.mzn"; % the actual isBetter predicate
include "pvs_search.mzn"; % search strategies for SPD better
include "pvs_spd.mzn";


% ==============================================================
% Now the actual model begins -> add soft constraints
var 1..3: x; 
var 1..3: y;
var 1..3: z;

% read as "soft constraint c1 is satisfied if and only if x + 1 = y"
constraint x + 1 = y <-> satisfied[1];
constraint z = y + 2 <-> satisfied[2];
constraint x + y <= 3 <-> satisfied[3];

% soft constraint specific for this model
nScs = 3;

% constraint relationships 
%    c1
%  c2  c3
% three soft constraints c1, c2, c3
%    c2 less imp than c1,
%    c3 less imp than c1,  

nCrEdges = 2;
crEdges = [| 2, 1 | 3, 1 |];

% uses a specific branch-and-bound search to perform optimization
solve 
:: int_search(satisfied, input_order, indomain_min, complete)
search time_limit(3000, strictlyBetterBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;

output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x), y  -> \(y), z -> \(z)"];

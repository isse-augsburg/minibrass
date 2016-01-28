% A really simplistic over-constrained model
% X: {x,y,z} D_i = {1,2,3}, i in X
%    * c1: x + 1 = y 
%    * c2: z = y + 2
%    * c3: x + y <= 3
% -------------------------------------------

include "minisearch.mzn"; % include the search minisearch lite
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/pvs_spd.mzn";
include "soft_constraints/pvs_search.mzn";

var 1..3: x; 
var 1..3: y;
var 1..3: z;

% read as "soft constraint c1 is satisfied iff x + 1 = y"
constraint x + 1 = y <-> satisfied[1];
constraint z = y + 2 <-> satisfied[2];
constraint x + y <= 3 <-> satisfied[3];

% soft constraint data specific to this model
nScs = 3; % number of soft constraints

% Constraint Relationships: 
%    c1
%  c2  c3
% -----------------------------------------
% three soft constraints c1, c2, c3
%    c2 less imp than c1,
%    c3 less imp than c1,  

nCrEdges = 2;
crEdges = [| 2, 1 | 3, 1 |];

% uses a specific branch-and-bound search to perform optimization
solve 

search onlyNotDominatedBAB(violatedScs) /\ 
       if hasSol() then print() 
       else print("No solution found\n") endif;

output ["Obj: \(penSum) by violating {\(violatedScs)}: " ++
        " x=\(x), y=\(y),z=\(z)"];

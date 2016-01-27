% A really simplistic over-constrained model
% X: {x,y,z} D_i = {1,2,3}, i in X
%    * c1: x + 1 = y 
%    * c2: z = y + 2
%    * c3: x + y <= 3
% -------------------------------------------

% import libraries as bundle for a quick start
include "soft_constraints/minizinc_bundle.mzn";

var 1..3: x; 
var 1..3: y;
var 1..3: z;

% read as "soft constraint c1 is satisfied iff x + 1 = y"
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
minimize penSum;

output ["Obj: \(penSum) by violating {\(violatedScs) } " ++ 
        " : x -> \(x), y  -> \(y), z -> \(z)"];

include "minisearch.mzn"; 
include "soft_constraints/soft_constraints.mzn"; 
include "soft_constraints/pvs_search.mzn"; 
include "soft_constraints/tpd_worse.mzn"; 
include "soft_constraints/pvs_tpd.mzn";
nScs = 3; var 1..3: x; 

constraint x = 1 <-> violated[1];
constraint x = 2 <-> violated[2];
constraint x = 3 <-> violated[3];

nCrEdges = 2; crEdges = [| 2, 1 | 3, 1 |];
% solution degrees are explored in order {3}, {2}, {1}
solve 
:: int_search([x], input_order, indomain_max, complete)
search strictlyBetterBAB(violatedScs);

output [" Obj: \(penSum) violating {\(violatedScs)}: x=\(x)"];

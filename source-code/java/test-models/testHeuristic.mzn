include "testHeuristic_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; 

var 1..3: x;
var 1..3: y;
var 1..3: z;

include "alldifferent.mzn";

constraint alldifferent([x,y,z]);
solve 
:: int_search([x,y,z], input_order, indomain_max, complete)
search pvs_BAB();

output ["x = \(x); y = \(y)"] ++ [ "\nValuations:  mbr_overall_cr1 = \(mbr_overall_cr1)\n"];

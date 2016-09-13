include "testMin_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; 

var bool: x;
var bool: y;

solve 
:: int_search([x,y], input_order, indomain_max, complete)
search pvs_BAB();

output ["x = \(x); y = \(y)"] ++ [ "\nValuations: mbr_overall_cr1 = \(mbr_overall_cr1); mbr_overall_cr2 = \(mbr_overall_cr2)\n"];

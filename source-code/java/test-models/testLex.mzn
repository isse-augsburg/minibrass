include "testLex_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; 

set of int: DOM = 1..3;
var DOM: x;
var DOM: y;

solve 
:: 
seq_search( [ 
  int_search([x], input_order, indomain_max, complete),
  int_search([y], input_order, indomain_min, complete)
])
search pvs_BAB();

output ["x = \(x); y = \(y)"] ++ [ "\nValuations: mbr_overall_cr1 = \(mbr_overall_cr1); mbr_overall_cr2 = \(mbr_overall_cr2)\n"];

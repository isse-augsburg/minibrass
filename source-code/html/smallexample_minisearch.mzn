
include "smallexample_minisearch_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
:: int_search([x,y,z], input_order, indomain_min, complete)
search pvs_BAB();

output ["x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations:  overall = \(topLevelObjective)\n"];

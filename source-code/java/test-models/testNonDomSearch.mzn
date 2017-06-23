include "testNonDomSearch_o.mzn"; % output of MiniBrass
include "soft_constraints/pvs_gen_search.mzn"; % for generic branch and bound

% the basic, "classic" CSP 
var 1..3: x;

% additional hard constraints would be here 

solve 
:: int_search([x], input_order, indomain_max, complete)
search pvs_BAB_NonDom(); % search procedure in MiniSearch  

output ["x = \(x)"] ++ 
       [ "\nValuations:  topLevelObjective = \(topLevelObjective)\n"];

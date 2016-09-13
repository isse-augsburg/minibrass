
include "classic_o.mzn"; % output of minibrass


% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
:: int_search([x,y,z], input_order, indomain_min, complete)
minimize topLevelObjective;

output ["Intermediate solution: x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations:  topLevelObjective = \(topLevelObjective)\n"];
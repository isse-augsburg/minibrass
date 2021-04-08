% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
:: int_search([x,y,z], input_order, indomain_min, complete)
%search pvs_BAB();
satisfy;

output ["x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations:  mbr_overall_cr1 = \(topLevelObjective)\n"];


include "classic_o.mzn"; % output of minibrass
include "soft_constraints/minibrass.mzn"; % for minibrass-search style using the generated predicate

% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

var 0..10: firstObjective;
constraint firstObjective = 2*y; 

solve 
%:: int_search([x,y,z], input_order, indomain_min, complete)
search presolveMin(firstObjective,0.15) /\ miniBrass();




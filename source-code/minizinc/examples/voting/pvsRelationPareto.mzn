include "pvsRelationPareto_o.mzn";
include "soft_constraints/minibrass.mzn"; 

var 1..3: a; 

solve 
:: int_search([a], input_order, indomain_min, complete)
search miniBrass();

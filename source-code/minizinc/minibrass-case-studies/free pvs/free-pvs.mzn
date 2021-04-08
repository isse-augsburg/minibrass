% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
:: int_search([x,y,z], input_order, indomain_min, complete)
satisfy;


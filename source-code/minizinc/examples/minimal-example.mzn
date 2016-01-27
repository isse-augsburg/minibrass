% my alltime favorite small example 

var 1..3: x; 
var 1..3: y;
var 1..3: z;

int: pc1 = 3;
int: pc2 = 1;
int: pc3 = 1;

var {0, pc1}: penC1;
var {0, pc2}: penC2;
var {0, pc3}: penC3;

var int: penSum = penC1 + penC2 + penC3;

constraint x + 1 = y <-> penC1 = 0;
constraint z = y + 2 <-> penC2 = 0;
constraint x + y <= 3 <-> penC3 = 0;

%constraint penC1 = 0 /\ penC2 = 0;
%constraint penSum = 0;
solve 
%:: int_search([penC1,penC2,penC3, x, y, z], input_order, indomain_min, complete)
:: int_search([penC1,penC2,penC3, x, y, z], input_order, indomain_min, complete)
minimize penSum;

output ["x -> \(x), y  -> \(y), z -> \(z)"];

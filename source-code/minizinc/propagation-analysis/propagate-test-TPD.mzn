% minimal example to explore propagation capabilities

include "../soft_constraints.mzn"; % model additions for soft constraint business
include "../spd_worse.mzn"; % the actual isBetter predicate
include "../tpd_worse.mzn"; % the actual isBetter predicate

nScs = 3;

var 0..1: x; % x *has* to be 2, I should stop my search at x = 1 
var 0..1: y; 
var 0..1: z;

array[1..2] of var bool: dummies; 

penalties = [3,1,1];

nCrEdges = 2;
crEdges = [| 2, 1 | 3, 1 |];

constraint x = 1 <-> satisfied[1];
constraint y = 1 <-> satisfied[2];
constraint z = 1 <-> satisfied[3];
constraint not spd_worse({1}, violatedScs,SOFTCONSTRAINTS,crEdges);
%constraint not tpd_worse({1}, violatedScs,SOFTCONSTRAINTS,crEdges);
%constraint penSum < 3;

solve 
:: int_search([x]++dummies++[y,z], input_order, indomain_min, complete)
satisfy;

output ["x = \(x), violated: \(violatedScs), y = \(y), z = \(z)"];
array[1..3] of var 0..1: x; 

% costs should contain the list of costs in the canonical unfolding of x and y 
predicate cost_function_ternary(var int: x, var int: y,  var int: z, array[int] of int: costs, var int: costVariable );

% default definitions for MZN STD lib
include "soft_constraints/cost_functions.mzn";

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
var 0..10: cVar;

constraint cost_function_ternary(x[1], x[2], x[3], [5, 2, 1, 3, 4, 6, 2, 3, 5], cVar);

% a minimal-cost assignment would be 0  1, 0  at costs 1
solve minimize cVar;
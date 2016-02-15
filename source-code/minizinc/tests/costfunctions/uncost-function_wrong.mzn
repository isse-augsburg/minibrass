var 0..1: x; 

predicate cost_function_unary(var int: x, array[int] of int: costs, var int: costVariable );

% default definitions for MZN STD lib
include "soft_constraints/cost_functions.mzn";

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
var 0..10: cVar;

constraint cost_function_unary(x, [3, 2, 3], cVar);


solve minimize cVar;
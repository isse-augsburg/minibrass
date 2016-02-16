array[1..2] of var 0..1: x; 

% default definitions for MZN STD lib
include "soft_constraints/cost_functions.mzn";

var 0..10: cVar;

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
constraint cost_function_binary(x[1], x[2], [4, 3, 2, 4], cVar);

solve minimize cVar;

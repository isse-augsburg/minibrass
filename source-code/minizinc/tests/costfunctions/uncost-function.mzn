var 0..1: x; 


% default definitions for MZN STD lib
include "soft_constraints/cost_functions.mzn";

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
var 0..10: cVar;

constraint cost_function_unary(x, [3, 2], cVar);


solve minimize cVar;
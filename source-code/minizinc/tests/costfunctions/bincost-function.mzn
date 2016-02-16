array[1..2] of var 0..1: x; 

% costs should contain the list of costs in the canonical unfolding of x and y 
predicate cost_function_binary(var int: x, var int: y, array[int] of int: costs, var int: costVariable );

% default definitions for MZN STD lib
%include "soft_constraints/cost_functions.mzn";

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
var 0..10: cVar;

constraint cost_function_binary(x[1], x[2], [4, 3, 2, 4], cVar);

solve satisfy;

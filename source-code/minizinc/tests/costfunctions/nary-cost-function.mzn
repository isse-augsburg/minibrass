array[1..4] of var 0..1: x; 

% costs should contain the list of costs in the canonical unfolding of x and y 
%predicate cost_function_binary(var int: x, var int: y, array[int] of int: costs, var int: costVariable );

% default definitions for MZN STD lib
include "soft_constraints/cost_functions.mzn";

% 0, 0 -> 4; 0, 1 -> 3; 1, 0 -> 2; 1, 1 -> 4
var 0..10: cVar;

% predicate cost_function_nary(array[int] of var int: vars, int: default_costs, array[int,int] of int: exceptions, array[int] of int: costs, var int: costVariable);

constraint cost_function_nary(x, 4, [| 0,0,0,1 | 1, 0,0,0 |], [3, 2], cVar);

solve satisfy;

/* cost_functions.mzn
-------------------------
Provides a default decomposition of cost functions
for weighted csp solvers. These map tuples in 
extensional form to cost values that are then added to an overall violation.

Example:
x, y | cost
0  0 | 4
0  1 | 3
1  0 | 2 
1  1 | 4

for a binary cost function. The assignment (x -> 1, y -> 0) would yield minimal overall
violation (2).

Toulbar2 has dedicated algorithms for such cost functions and can be accessed via Numberjack.
For all other solvers, this decomposition provides an encoding using table constraints
to represent arbitrary (finite-domain, up to ternary) functions

AS, ISSE Augsburg, part of MiniBrass, 2016 
*/

include "table.mzn";

% costs should provide the cost value for each domain value of x (in increasing order) 
% e.g. x \in 0..2 ->, costs = [4, 1, 3] would be interpreted as f(x -> 0) = 4, f(x -> 1) = 1, f(x -> 2) = 3
predicate cost_function_unary(var int: x, array[int] of int: costs, var int: costVariable )= 
let {
  array[int] of int: folded = [ x_ | x_ in dom(x)];  
}
in ( cost_function_unary_safe(folded, costs, x, costVariable)); 

predicate cost_function_unary_safe(array[int] of int: folded, array[int] of int: costs, var int: x, var int: costVariable) = 
assert(max(index_set(folded)) == max(index_set(costs)), "Dimensions of cost vector and flattened domains must agree, i.e., you should have \(max(index_set(folded))) cost entries.", 
   let {
     array[int] of int: tableDecomp = [ if(j == 1) then folded[i] else costs[i] endif | i in index_set(costs), j in 1..2 ];
   } in table([x,costVariable], array2d(index_set(costs), 1..2, tableDecomp)));
   
% costs should be provided for each element in the cartesian product of two domains, in increasing lexicographic order
% e.g. 
% x, y | cost
% 0  0 | 4
% 0  1 | 3
% 1  0 | 2 
% 1  1 | 4
% would be represented by the cost vector [4, 3, 2, 4]
predicate cost_function_binary(var int: x, var int: y, array[int] of int: costs, var int: costVariable ) = 
let {
  array[int] of int: folded = [ if(i == 1) then x_ else y_ endif | x_ in dom(x), y_ in dom(y), i in 1..2];
}
in (cost_function_binary_safe(folded, costs, x, y, costVariable) );

function var int: cost_function_binary_fn(var int: x, var int: y, array[int] of int: costs ) = 
let {
  var int: costVariable;
  constraint cost_function_binary(x, y, costs, costVariable);
}
in costVariable;


predicate cost_function_binary_safe(array[int] of int: folded, array[int] of int: costs, var int: x, var int: y, var int: costVariable) = 

assert(max(index_set(folded)) == 2*max(index_set(costs)), "Dimensions of cost vector and flattened domains must agree, i.e., you should have \(max(index_set(folded)) div 2) cost entries.", 
   let {
     int: dim = 2;
     array[int] of int: tableDecomp = [ if(j <= 2) then folded[(i-1)*dim + j] else costs[i] endif | i in index_set(costs), j in 1..3 ];
   } in table([x,y,costVariable], array2d(index_set(costs), 1..3, tableDecomp)));
   
% costs should be provided for each element in the cartesian product of three domains, in increasing lexicographic order
% e.g. 
% x, y, z | cost
% 0  0  0 | 5
% 0  0  1 | 2
% 0  1  0 | 1
% 0  1  1 | 3
% 1  0  0 | 4
% 1  0  1 | 6 
% 1  1  0 | 2 
% 1  1  1 | 3
% would be represented by the cost vector [5, 2, 1, 3, 4, 6, 2, 3], a minimal-cost assignment would be 0  1, 0 
predicate cost_function_ternary(var int: x, var int: y, var int: z, array[int] of int: costs, var int: costVariable ) = 
let {
  array[int] of int: folded = [ if(i == 1) then x_ elseif (i == 2) then y_ else z_ endif | x_ in dom(x), y_ in dom(y), z_ in dom(z), i in 1..3];
}
in (cost_function_ternary_safe(folded, costs, x, y, z, costVariable) );

predicate cost_function_ternary_safe(array[int] of int: folded, array[int] of int: costs, var int: x, var int: y, var int: z, var int: costVariable) = 

assert(max(index_set(folded)) == 3*max(index_set(costs)), "Dimensions of cost vector and flattened domains must agree, i.e., you should have \(max(index_set(folded)) div 3) cost entries.", 
   let {
     int: dim = 3;
      array[int] of int: tableDecomp = [ if(j <= 3) then folded[(i-1)*dim + j] else costs[i] endif | i in index_set(costs), j in 1..4 ];
   } in table([x,y,z,costVariable], array2d(index_set(costs), 1..4, tableDecomp)));
   
% The nary variant requires default_costs for all tuples and allows to list explicit exceptions, exceptions[i] should correspond to costs[i]
% e.g. 
% x, y | cost
% 0  0 | 4
% 0  1 | 3
% 1  0 | 2 
% 1  1 | 4
% could be represented by vars = [x,y], default_costs = 4, exceptions = [| 0,1 | 1, 0 |], costs = [3, 2] 
% ATTENTION: Currently not supported but only enabled for toulbar2 via Numberjack
predicate cost_function_nary(array[int] of var int: vars, int: default_costs, array[int,int] of int: exceptions, array[int] of int: costs, var int: costVariable );

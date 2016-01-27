% a very minimal example to provoke isa: nullptr bug
% ---------------------------------

set of int: range = 1..5;
var set of range: x;

predicate onlyEvens(var set of range: y) = 
(
 forall(r in y) (r mod 2 = 0)
);

constraint onlyEvens(x);
solve :: set_search([x], input_order, indomain_max, complete)
satisfy;


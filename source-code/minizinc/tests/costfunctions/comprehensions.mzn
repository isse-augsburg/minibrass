include "soft_constraints/cost_functions.mzn";

% shows an exemplary comprehension based cost function
var 0..1: x;
var 0..1: y; 
var 0..10: costVar;

/*
x y | 4 - (x + y)
------------------
0 0 | 4
0 1 | 3
1 0 | 3
1 1 | 2
*/
function int: f(int: x, int: y) = (
  4 - (x + y)
);

constraint cost_function_binary(x, y, [f(x_,y_) | x_ in dom(x), y_ in dom(y)], costVar);

solve minimize costVar;
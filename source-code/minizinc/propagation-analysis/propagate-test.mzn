% minimal example to explore propagation capabilities

include "../soft_constraints.mzn"; % model additions for soft constraint business
include "../spd_worse.mzn"; % the actual isBetter predicate
include "../tpd_worse.mzn"; % the actual isBetter predicate
include "../cr-types.mzn";

nScs = 3;

var 1..2: x; % x *has* to be 2, I should stop my search at x = 1 
var 1..2: y; 
var 1..2: z;

array[1..3] of var bool: dummies; 

penalties = [2,1,1];

nCrEdges = 2;
crEdges = [| 2, 1 | 3, 1 |];

constraint x = 2 <-> satisfied[1];
constraint y = 2 <-> satisfied[2];
constraint z = 2 <-> satisfied[3];
constraint spd_worse({1}, violatedScs,SOFTCONSTRAINTS,crEdges);
%constraint penSum < 2;
%constraint spd_better({1}, lb(violatedScs),SOFTCONSTRAINTS,edges);
% how can the solver immediately deduce that x = 1 is a bad idea?
% well quite simple, x = 1 -> violated[1] thus violatedScs \in 1..1, 1..2
% but 1..1 is kicked from violatedScs' domain by the propagation of the simple 
% inequality lhs != rhs in spd_better, thus only 1..2 remains in the domain of violatedScs
% and this value (regardless of assigning y = 1 or y = 2 isn't spd better!

% I need a somewhat more elaborate example to support my point

% my hypothesis remains that, as long as there are multiple values in the domain
% of the valuation, we do not propagate/prune anything, we were lucky this time
% as the variable got fixed by propagation

solve 
:: int_search([x]++dummies++[y,z], input_order, indomain_min, complete)
satisfy;

output ["x = \(x), violated: \(violatedScs), y = \(y)"];
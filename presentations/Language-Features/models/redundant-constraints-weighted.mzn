% minimal example to explore propagation capabilities
include "soft_constraints/soft_constraints.mzn"; 
include "soft_constraints/spd_worse.mzn"; 
include "soft_constraints/tpd_worse.mzn"; 
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";

var 1..2: x; var 1..2: y; var 1..2: z;
array[1..3] of var bool: dummies; 

penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, true) 
              | s in SOFTCONSTRAINTS];
nCrEdges = 2; nScs = 3;
crEdges = [| 2, 1 | 3, 1 |];

constraint x = 2 <-> satisfied[1];
constraint y = 2 <-> satisfied[2];
constraint z = 2 <-> satisfied[3];
constraint spd_worse({1}, violatedScs,SOFTCONSTRAINTS,crEdges);
% if we need to find an actually better solution than {1}
% (with violation 2), the penalty sum has to be strictly
% less than 2
constraint penSum < 2;

solve 
:: int_search([x]++dummies++[y,z], input_order, indomain_min, complete)
satisfy;

output ["x = \(x), violated: \(violatedScs), y = \(y)"];
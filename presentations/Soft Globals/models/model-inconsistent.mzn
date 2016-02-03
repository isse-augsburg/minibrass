include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/pvs_spd.mzn";

var 0..10: x; var 0..10:y;

nScs = 2;
nCrEdges = 2; crEdges = [| 2, 1 | 1, 2 |];
constraint x < y <-> satisfied[1];
constraint x + 4 = y <-> satisfied[2]; 

solve satisfy;

output["x = \(x), y = \(y), violatedScs = \(violatedScs)"];
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/tpd_worse.mzn";
include "soft_constraints/pvs_tpd.mzn";
var 1..3: x; var 1..3: y; var 1..3: z;

constraint x + 1 = y <-> satisfied[1];
constraint z = y + 2 <-> satisfied[2];
constraint x + y <= 3 <-> satisfied[3];
nScs = 3; nCrEdges = 2; crEdges = [| 2, 1 | 3, 1 |];

solve minimize penSum; 
output ["Obj: \(penSum) by x=\(x), y=\(y),z=\(z)"];

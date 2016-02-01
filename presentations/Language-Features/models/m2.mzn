include "minisearch.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/spd_worse.mzn"; % tpd_worse.mzn
include "soft_constraints/pvs_spd.mzn"; % pvs_tpd.mzn, pvs_weighted.mzn
include "soft_constraints/pvs_search.mzn";

var 0..10: x; var 0..10: y;
array[1..10] of var 0..1: dummies;

nScs = 2; nCrEdges = 1; crEdges = [| 2, 1 |];
constraint x < y <-> satisfied[1];
constraint x + 4 = y <-> satisfied[2]; 

solve
search strictlyBetterBAB(violatedScs) /\ print();
output["x = \(x), y = \(y), violatedScs = \(violatedScs)"];

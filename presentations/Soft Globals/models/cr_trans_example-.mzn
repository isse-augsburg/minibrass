include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_consistency.mzn";
nScs = 3; penalties = [1 | i in 1..nScs];
nCrEdges = 2;
crEdges = [| 2, 1 | 3, 2  |];

array[int, 1..2] of int: crTempEdges = transClosure(SOFTCONSTRAINTS, crEdges);
set of int: TrEdges = index_set_1of2(crTempEdges);
int: nTrEdges = max(TrEdges);

var 0..3: x; solve satisfy;

output["crEdges=\(crEdges)\ncrTempEdges=\(crTempEdges)\nnTrEdges = \(nTrEdges)"];

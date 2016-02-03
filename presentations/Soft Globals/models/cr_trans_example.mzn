include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_consistency.mzn";
nScs = 3; penalties = [1 | i in 1..nScs];

array[int, 1..2] of int: crTempEdges = [|  3, 2  | 2, 1 ||];
crEdges = transClosure(SOFTCONSTRAINTS, crTempEdges);
nCrEdges = max(crEdges);
var 0..3: x; solve satisfy;

output["crEdges=\(crEdges)\ncrTempEdges=\(crTempEdges)"++
       "\nnCrEdges = \(nCrEdges)"];

include "../cr-consistency.mzn";

% Verifies that a cyclic
% constraint relationship is detected

set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

nCrEdges = 3;
crEdges = [| 1, 2 | 2, 3 | 3, 1 |];

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");
solve satisfy; 


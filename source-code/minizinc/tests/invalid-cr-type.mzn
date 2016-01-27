include "../cr-consistency.mzn";

% Verifies that an incorrect 
% edges matrix (second dim is 3)
% is detected by consistentCR

set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..3] of SOFTCONSTRAINTS: crEdges;

nCrEdges = 2;
crEdges = [| 1, 2, 3 | 2, 1, 3 |];

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");
solve satisfy; 
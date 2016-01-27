include "../cr-consistency.mzn";

% Verifies that an incorrect 
% edges matrix pointing to 
% a value not in the set of soft constraints
% is properly detected

set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS union {5}: crEdges;

nCrEdges = 2;
crEdges = [| 1, 2 | 2, 5 |];

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");
solve satisfy; 
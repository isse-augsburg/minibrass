include "../cr-consistency.mzn";

% Verifies that a correctly specified 
% constraint relationship does not have 
% problems with passing the consistenCR test

set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

nCrEdges = 3;
crEdges = [| 1, 2 | 2, 3 | 1, 3 |];

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");
solve satisfy; 


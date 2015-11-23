% tests the transitive closure calculation
include "../cr-consistency.mzn";

% Verifies that a correctly specified 
% constraint relationship does not have 
% problems with passing the consistenCR test

set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

nCrEdges = 2;
crEdges = [| 2, 1 | 3, 2  |];

% there is one transitive edge, 4 -> 1, will we find it?

array[int, 1..2] of int: crTempEdges = transClosure(SOFTCONSTRAINTS, crEdges);
set of int: TrEdges = index_set_1of2(crTempEdges);
int: nTrEdges = max(TrEdges);
solve satisfy; 

output["crEdges=\(crEdges)\ncrTempEdges=\(crTempEdges)\nnTrEdges = \(nTrEdges)"];

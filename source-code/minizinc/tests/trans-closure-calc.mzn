% tests the transitive closure calculation
include "../cr-consistency.mzn";

% Checks that we get transitive edges
% in the appropriate data format
set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS union {5}: crEdges;

nCrEdges = 2;

% switch between those lines to see consistency check in action
crEdges = [| 2, 1 | 3, 2  |];
%crEdges = [| 2, 1 | 3, 5  |];

% there is one transitive edge, 3 -> 1, will we find it?

array[int, 1..2] of int: crTempEdges = transClosure(SOFTCONSTRAINTS, crEdges);
set of int: TrEdges = index_set_1of2(crTempEdges);
int: nTrEdges = max(TrEdges);

% this needs to be checked separately, don't want
% to force cyclic check on transitive closure
constraint consistentCR(SOFTCONSTRAINTS, crEdges);
solve satisfy; 

output["crEdges=\(crEdges)\ncrTempEdges=\(crTempEdges)\nnTrEdges = \(nTrEdges)"];

% constraint-relationship-types
int: nCrEdges;

% switched to "int" for first axis so we can write
% crEdges = transClosure(SOFTCONSTRAINTS, crTempEdges);
% nCrEdges = max(crEdges); 
array[int, 1..2] of SOFTCONSTRAINTS: crEdges;

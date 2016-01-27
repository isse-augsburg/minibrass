include "../cr-weighting.mzn";

% constraint-relationship-types
set of int: SOFTCONSTRAINTS = 1..3;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

% check consistency
nCrEdges = 2;
crEdges = [| 3, 1 | 2, 1 |];

bool: useSPD = false;

array[SOFTCONSTRAINTS] of int: penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, useSPD) | s in SOFTCONSTRAINTS];
 

solve satisfy;

output ["penalties = \(penalties)"];
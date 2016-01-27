include "../cr-weighting.mzn";

% tests a slightly more complicated constraint relationship
% Skier A from the SGAI'13 paper

% constraint-relationship-types
set of int: SOFTCONSTRAINTS = 1..6;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

% check consistency
nCrEdges = 7;
crEdges = [| 2, 1 |
             5, 1 |
             3, 1 |
             4, 1 |
             6, 5 | 
             6, 4 |
             6, 3 |];

bool: useSPD = false;

array[SOFTCONSTRAINTS] of int: penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, useSPD) | s in SOFTCONSTRAINTS];
% we use the ordering 
% LW  ABS  FP  VT  OE  LI 
%  1   2   3   4   5   6

% result should be 
% SPD:  [3,  1, 2, 2, 2, 1]
% TPD:  [11, 1, 2, 2, 2, 1]
constraint (useSPD /\ penalties = [3,  1, 2, 2, 2, 1] ) \/ ( not useSPD /\ penalties = [11, 1, 2, 2, 2, 1] );

solve satisfy;

output ["penalties = \(penalties)"];
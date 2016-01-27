include "../cr-weighting.mzn";

% tests a slightly more complicated constraint relationship
% Boarder B from the SGAI'13 paper

% constraint-relationship-types
set of int: SOFTCONSTRAINTS = 1..6;

int: nCrEdges;
array[1..nCrEdges, 1..2] of SOFTCONSTRAINTS: crEdges;

% check consistency
% we use the ordering 
% LW  ABS  FP  VT  OE  LI 
%  1   2   3   4   5   6
int: LW = 1; int: ABS = 2; int: FP = 3;
int: VT = 4; int: OE = 5; int: LI = 6;

nCrEdges = 6;
crEdges = [| FP,  VT |  
             ABS, VT |
             LI, FP |
             LW, LI |
             LW, ABS | 
             OE, LW |];

bool: useSPD = false;

array[SOFTCONSTRAINTS] of int: penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, useSPD) | s in SOFTCONSTRAINTS];

% result should be 
% SPD:  [2, 3, 4, 5, 1, 3]
% TPD:  [2, 4, 8, 23, 1, 4]
constraint (useSPD /\ penalties = [2, 3, 4, 5, 1, 3]) \/ ( not useSPD /\ penalties = [2, 4, 8, 23, 1, 4] );

solve satisfy;

output ["penalties = \(penalties)"];
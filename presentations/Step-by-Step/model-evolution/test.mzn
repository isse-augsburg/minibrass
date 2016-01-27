include "globals.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_types.mzn"; % graph types
include "soft_constraints/cr_weighting.mzn"; % weighting functions
int: n; array[1..n] of var 1..n: queens; n = 8;

solve :: int_search(queens, first_fail, indomain_median, complete) 
minimize penSum; output["penSum = \(penSum), queens = \(queens), \(penalties)"];
constraint all_different(queens); 
constraint all_different([queens[i]+i | i in 1..n]); 
constraint all_different([queens[i]-i | i in 1..n]); 
% soft constraint business
nScs = 3; 
penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, false) 
              | s in SOFTCONSTRAINTS];
constraint queens[n div 2] = n div 2 <-> satisfied[1] ; 
constraint queens[2] = queens[1] + 2 <-> satisfied[2];
constraint queens[3] = queens[2] + 2 <-> satisfied[3];

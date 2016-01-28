include "globals.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/spd_worse.mzn"; %  solution ordering
include "soft_constraints/pvs_spd.mzn"; %  concrete PVS
include "minisearch.mzn"; % custom search combinators
include "soft_constraints/pvs_search.mzn";
int: n; array[1..n] of var 1..n: queens; n = 8; nScs = 3; 
output["queens= \(queens), obj = \(violatedScs) "];
solve :: int_search(queens, first_fail, indomain_median, complete) 
search strictlyBetterBAB(violatedScs) /\ print();
constraint all_different(queens); 
constraint all_different([queens[i]+i | i in 1..n]); 
constraint all_different([queens[i]-i | i in 1..n]); 
constraint queens[n div 2] = n div 2 <-> satisfied[1] ; 
constraint queens[2] = queens[1] + 2 <-> satisfied[2];
constraint queens[3] = queens[2] + 2 <-> satisfied[3];

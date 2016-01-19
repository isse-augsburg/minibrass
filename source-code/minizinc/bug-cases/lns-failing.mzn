% now just an experimental lns with only predefined variables
include "minisearch.mzn";
array[1..2] of var 0..10: xs;
constraint xs[2] <= 8;

var 0..18: obj;

constraint obj = xs[1] + xs[2];

constraint xs[1] < xs[2];

solve 

search lns_max(obj, xs, 5, 0.5) /\ if hasSol() then print() else print("No solution found\n") endif;


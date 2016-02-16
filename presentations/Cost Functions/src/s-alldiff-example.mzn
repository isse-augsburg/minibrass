include "soft_constraints/soft_alldifferent.mzn";

int: n = 5; int: m = 4;
set of int: STUDENT = 1..n; 
set of int: PROJECT = 1..m;
array[STUDENT] of var PROJECT: x;
var int: maxPerProj = 2;
constraint soft_alldifferent(x, maxPerProj, true);
solve satisfy;
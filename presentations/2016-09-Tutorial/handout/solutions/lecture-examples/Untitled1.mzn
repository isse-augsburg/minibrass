int: n = 4; 
set of int: NURSES = 1..n; 

int: m = 3; 
set of int: SHIFTS = {1,2,3};

int: d = 5;
set of int: DAYS = 1..d;

include "globals.mzn";


array[NURSES,DAYS] of var SHIFTS union {0}: worksShift;

constraint forall(n in NURSES) (
       global_cardinality_low_up([worksShift[n,d] | d in DAYS], [0,1,2,3], [1,0,0,0], [1,5,5,2] ) );
solve satisfy; 
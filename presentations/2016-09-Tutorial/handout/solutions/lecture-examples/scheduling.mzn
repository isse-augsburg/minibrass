int: morning = 1; int: noon = 2; int: night = 3; 
set of int: SHIFTS = {morning, noon, night};

int: nStaff; 
set of int: STAFF = 1..nStaff; 

include "global_cardinality_low_up.mzn";
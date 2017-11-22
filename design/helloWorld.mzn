include "rostering_o.mzn"; % the compiled mzn-output
include "soft_constraints/minibrass.mzn"; 

int: day = 1; int: night = 2; int: off = 3;
set of int: SHIFTS = {day, night, off};

set of int: NURSES = 1..3;
array[NURSES] of var SHIFTS: roster;

solve search miniBrass();
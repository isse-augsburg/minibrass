include "nurseHelloWorld_o.mzn"; /* the compiled mzn-output */
include "soft_constraints/minibrass.mzn";  /* MiniBrass prepared searches */

/* the actual decision variables and *hard* constraints */
int: day = 1; int: night = 2; int: off = 3;
set of int: SHIFTS = {day, night, off};

set of int: NURSES = 1..3;
array[NURSES] of var SHIFTS: roster;

/* one nurse has to take the night shift */

constraint exists(n in NURSES) (roster[n] = night);

solve search miniBrass();

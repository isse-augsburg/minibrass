int: n = 8; 
set of int: DIGITS = 0..9;

array[1..n] of var DIGITS: x; 

constraint sum(i in 1..n)(x[i]) = product(i in 1..n)(x[i]);

include "globals.mzn";

%constraint increasing(x);
constraint forall(i in 1..n-1) (x[i] <= x[i+1]);

solve satisfy;
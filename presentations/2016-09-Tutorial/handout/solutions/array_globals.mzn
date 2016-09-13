int: n = 10; 
set of int: DIGITS = 0..9;

array[1..n] of var DIGITS: x; 

constraint sum(x) = product(x);
include "globals.mzn";
constraint increasing(x);
solve satisfy;
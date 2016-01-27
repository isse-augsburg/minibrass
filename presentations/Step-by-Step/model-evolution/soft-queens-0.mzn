include "globals.mzn";
int: n; array[1..n] of var 1..n: queens; n = 8;

solve :: int_search(queens, first_fail, indomain_median, complete) 
satisfy;

constraint all_different(queens); 
constraint all_different([queens[i]+i | i in 1..n]); 
constraint all_different([queens[i]-i | i in 1..n]); 

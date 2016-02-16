include "alldifferent.mzn";
int: n = 5; int: m = 6;
set of int: STUDENT = 1..n; 
set of int: PROJECT = 1..m;
array[STUDENT] of var PROJECT: x;

constraint alldifferent(x);
solve satisfy;
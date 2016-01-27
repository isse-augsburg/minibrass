int: n = 5; int: s = 4;
array[1..n] of int: a = [-7, -3, -2, 5, 8];

array[1..n] of var 0..1: x;

constraint sum(i in 1..n) (x[i]*a[i]) = s;
solve satisfy;
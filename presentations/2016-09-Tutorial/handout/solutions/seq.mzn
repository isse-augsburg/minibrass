% seq.mzn
set of int: DOM = 0..3;
int: n; 
array[1..n] of var DOM: x; 

constraint x[1] = 0;
constraint x[n] = 3; 

constraint forall(i in 1..n-1) (x[i] + x[i+1] <= 3);
constraint forall(i in 1..n where i mod 3 = 0) (x[i] >= 2);

var int: obj = sum(x);
solve maximize obj;

output["\(obj) = \(x)"];
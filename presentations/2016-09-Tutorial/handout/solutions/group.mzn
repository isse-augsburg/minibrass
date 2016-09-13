int: n;
array[1..n,1..n] of var bool: friend;

array[1..n] of var 1..n: placement;

include "alldifferent.mzn";

constraint alldifferent(placement);

var int: obj = sum(i in 1..n-1)(friend[placement[i], placement[i+1]] );

solve maximize obj;

output ["Obj = \(obj); \(placement)"];
% Weighted Constraint Type

% meta-parameters
int: nScs = 3;

% parameters
int: k; constraint assert(k > 0, "K has to be positive");
set of int: VAL = 0..k;

array[1..nScs] of 1..k: weights = [2, 1, 1]; 

predicate is_worse_weighted(var int: x, var int: y, array[1..nScs] of 1..k: weights) = x >= y;

function var VAL: weighted_sum(array[1..nScs] of var bool: b, array[1..nScs] of 1..k: weights) = 
  sum(i in 1..nScs) (bool2int(b[i]) * weights[i]  ) ;  

VAL: top = 0;

/* 2. Valued Constraints

PVSType<E, S>

Parameters:
array[1..n] of 1..k: weights

Element type (E)                   ... int (0 .. k, to be precise, called VAL) 
Specification type (S)             ... bool
Aggregation function : (S^n -> E)  ... sum(array[1..nScs] of var bool: b) = 0..k
Ordering relation R sub E x E      ... is_worse_weighted
Best element                       ... 0

*/ 

array[1..nScs] of var bool: x; 
var VAL: overall;

constraint overall = weighted_sum(x, weights);

constraint is_worse_weighted(x[2], x[3], weights) /\ x[2] != x[3];

solve maximize overall;
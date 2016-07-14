% Weighted Constraint Type

% parameters
int: k; constraint assert(k > 0, "K has to be positive");
predicate is_worse_weighted(var int: x, var int: y, 
  par int: nScs, par int: k, array[int] of par int: weights) = 
  x >= y;

function var int: weighted_sum(array[int] of var bool: b, 
                               par int: nScs, par int: k, array[int] of par int: weights) =
  sum(i in index_set(b)) ( (1 - bool2int(b[i])) * weights[i]);

/* 1. Cost Function Networks 

PVSType<E, S>

Element type (E)                   ... int (0 .. k, to be precise) 
Specification type (S)             ... bool 
Aggregation function : (S^n -> E)  ... weighted_sum(array[SOFTCONSTRAINTS] of var bool: b, array[SOFTCONSTRAINTS] of par int: w) = 0..k
Ordering relation R sub E x E      ... is_worse_weighted
Best element                       ... 0


array[1..3] of var 0..k: x; 
var 0..k: overall;

constraint overall = sum(x);

constraint is_worse_weighted(x[2], x[3]) /\ x[2] != x[3];
solve maximize overall;

*/ 

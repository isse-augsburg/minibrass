% Weighted Constraint Type

% parameters
int: k; constraint assert(k > 0, "K has to be positive");
predicate is_worse_weighted(var int: x, var int: y) = x >= y;

/* 1. Cost Function Networks 

PVSType<E, S>

Element type (E)                   ... int (0 .. k, to be precise) 
Specification type (S)             ... int 
Aggregation function : (S^n -> E)  ... sum(array[SOFTCONSTRAINTS] of var int: b) = 0..k
Ordering relation R sub E x E      ... is_worse_weighted
Best element                       ... 0

*/ 

array[1..3] of var 0..k: x; 
var 0..k: overall;

constraint overall = sum(x);

constraint is_worse_weighted(x[2], x[3]) /\ x[2] != x[3];
solve maximize overall;
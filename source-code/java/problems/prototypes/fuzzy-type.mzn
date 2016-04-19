% Fuzzy Constraint Type

% meta-parameters
int: nScs = 3;

% parameters n.A.

set of float: FVAL = 0.0 .. 1.0; 

predicate is_worse_fuzzy(var float: x, var float: y) = x <= y;

/* Fuzzy Constraints 

PVSType<E, S>

Element type (E)                   ... float (0.0 .. 1.0, to be precise, called FVAL) 
Specification type (S)             ... float (0.0 .. 1.0) 
Aggregation function : (S^n -> E)  ... sum(array[SOFTCONSTRAINTS] of var int: b) = 0..k
Ordering relation R sub E x E      ... is_worse_weighted
Best element                       ... 0

*/ 

array[1..nScs] of var FVAL: x; 

% make it tractable for now 
constraint forall(i in 1..nScs )(x[i] = 0.25 \/ x[i] = 0.52); 
constraint x[1] > x[2];

var FVAL: overall;

constraint overall = min(x);
%constraint x[2] < x[3];
constraint is_worse_fuzzy(x[2], x[3]);
solve maximize overall;
% problem with float predicates
set of float: FVAL = 0.0 .. 1.0;

var FVAL: x; var FVAL: y;

predicate lessThan(var float: x_, var float: y_) = x_ < y_ ;

function var bool: lessThanFunc(array[1..2] of var float: x) = x[1] < x[2];

% make it discretized, for testing
constraint (x = 0.05 \/ x = 0.55 \/ x = 0.75) /\ (y = 0.45 \/ y = 0.5 \/ y = 0.9);

% this works
constraint x < y; 

% this doesn't 
constraint lessThan(x, y);

% neither does this 
%constraint lessThanFunc([x,y]);

solve maximize x+y;
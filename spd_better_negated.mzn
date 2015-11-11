% just to test my predicate in a more negated context?
include "spd_better.mzn";

int: nConstraints = 3;
set of int: CONSTRAINTS = 1..nConstraints;

var set of CONSTRAINTS: lhs;
var set of CONSTRAINTS: rhs;
int: c1 = 1;
int: c2 = 2; % c1 <- c2, c1 is more important than c2
int: c3 = 3; % c1 <- c3, c1 is also more important than c3
int: nEdges = 2;
array[1..nEdges, 1..2] of CONSTRAINTS: edges = [| c2, c1 
                                                | c3, c1 |];
%constraint spd_better(lhs,rhs, CONSTRAINTS, edges);

constraint spd_better(1..3, 1..2, CONSTRAINTS, edges);
%constraint not spd_better(1..2, 1..3, CONSTRAINTS, edges);


%constraint not spd_better(1..3, 1..2, CONSTRAINTS, edges);

constraint not spd_better(lhs, 1..2, CONSTRAINTS, edges);

solve satisfy;
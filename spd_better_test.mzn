% pretty much only test code 
% -------------------------------- 
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

%lhs = {3};
%rhs ={2};

constraint lhs != rhs;
constraint spd_better(lhs,rhs, CONSTRAINTS, edges);

solve satisfy; 

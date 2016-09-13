
% problem data 
int: n; set of int: ROBOTS = 1..n;
int: m; set of int: TASKS = 1..m;
array[ROBOTS,TASKS] of int: profit;

% decisions
array[ROBOTS] of var TASKS: allocation;

% goal
var int: obj = sum(r in ROBOTS) (profit[r, allocation[r]] );
solve maximize obj ;

% have robots work on different tasks
%constraint forall(r1 in ROBOTS, r2 in ROBOTS where r1 < r2) 
%  ( allocation[r1] != allocation[r2] );
include "alldifferent.mzn";
constraint alldifferent(allocation);
output ["Obj = \(obj); allocation = \(allocation)"];
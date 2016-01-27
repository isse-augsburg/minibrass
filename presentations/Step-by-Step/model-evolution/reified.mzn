include "globals.mzn";
include "soft_constraints/soft_constraints.mzn";
array[1..5] of var 0..5: x;
constraint alldifferent(x); % hard constraint

% but it would be nice to have x increase ...
nScs = 1; penalties = [1];
constraint increasing(x) <-> satisfied[1];

solve maximize bool2int(satisfied[1]);
output["satisfied[1] = \(satisfied[1]), x = \(x)"];
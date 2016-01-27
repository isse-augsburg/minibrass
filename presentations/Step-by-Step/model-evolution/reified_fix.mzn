include "globals.mzn";
include "soft_constraints/soft_constraints.mzn";
array[1..5] of var 0..5: x;
constraint alldifferent(x); % hard constraint
nScs = 1; penalties = [1];
% copied from share/minizinc/std/increasing_int.mzn
predicate my_increasing_int(array[int] of var int: x) =
    forall(i in index_set(x) diff { min(index_set(x)) }) 
       (x[i-1] <= x[i]);

constraint my_increasing_int(x) <-> satisfied[1];
solve maximize bool2int(satisfied[1]);
output["satisfied[1] = \(satisfied[1]), x = \(x)"];
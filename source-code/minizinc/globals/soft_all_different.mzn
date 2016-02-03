include "soft_constraints/soft_all_different_int.mzn";


% just a synonym for the other spelling of alldifferent
predicate soft_alldifferent(array[int] of var int: x, var int: cost, bool: useDec) =
    soft_all_different_int(x, cost, useDec);

predicate soft_all_different(array[int] of var int: x, var int: cost, bool: useDec) =
    soft_all_different_int(x, cost, useDec);

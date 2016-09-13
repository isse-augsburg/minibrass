% Weighted Constraint Type


predicate is_worse_fuzzy(var float: x, var float: y, par int: nScs) = 
  x < y;

function var float: min(array[int] of var float: b, 
                     par int: nScs) =
  min(b);

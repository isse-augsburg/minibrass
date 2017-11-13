% Cost function network Type

predicate is_worse_weighted(var int: x, var int: y, 
  par int: nScs, par int: k) = 
  x > y;

predicate is_greater_weighted(var int: x, var int: y, 
  par int: nScs, par int: k) = 
  x < y;
  
function var int: sum(array[int] of var int: b, 
                               par int: nScs, par int: k) =
  sum(b);


predicate is_worse_boolean(var bool: o1, var bool: o2,
  par int: nScs, par bool: conjunction) = 
 o1 == false /\ o2 == true
;

function var bool: combine_boolean(array[int] of var bool: b, 
                       par int: nScs, par bool: conjunction) =
  if conjunction then  sum(b) = max(index_set(b)) else sum(b) > 0 endif 
  ;

% Parametrized network Type

include "table.mzn";

predicate is_worse_relation(var int: o1, var int: o2,
  par int: nScs, par int: k,  par int: topVal, array [int,int] of int: tuples) = 
let {
  constraint table([o1,o2], tuples);
} in
(
 true
);


function var int: max(array[int] of var int: b, 
                               par int: nScs, par int: k, par int: topVal, array [int,int] of int: tuples) =
  max(b);



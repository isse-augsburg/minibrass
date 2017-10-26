% Parametrized network Type

include "table.mzn";

% ========================= explicit relation
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


% ========================= explicit boolean

predicate is_worse_bool(var bool: o1, var bool: o2,
  par int: nScs, par int: k, set of int: approvedSet) = 
 o1 == false /\ o2 == true
;

function var bool: and(array[int] of var bool: b, 
                       par int: nScs, par int: k, set of int: approvedSet) =
  sum(b) > 0;

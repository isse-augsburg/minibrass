/* Constraint Relationships 

PVSType<E, S>

Element type (E)                   ... set of 1..nScs
Specification type (S)             ... bool
Aggregation function : (S^n -> E)  ... link_booleans
Ordering relation R sub E x E      ... is_worse_cr
Best element                       ... {}

*/ 

include "link_set_to_booleans.mzn";

function var set of int: link_booleans(array[int] of var bool: b) =
let {
  
  var set of index_set(b): resultSet;
  constraint link_set_to_booleans(resultSet,b); 
} in resultSet;

% gives us access to constraint relationship predicates 
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/tpd_worse.mzn";

predicate is_worse_cr(var set of 1..nScs: violated1, var set of 1..nScs: violated2,
                      array[int, 1..2] of par 1..nScs: crEdges, par bool: useSPD) =
if useSPD then
  spd_worse(violated1, violated2, crEdges)
else
  tpd_worse(violated1, violated2, crEdges
endif;

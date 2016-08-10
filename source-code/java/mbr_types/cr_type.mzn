/* Constraint Relationships 

PVSType<E, S>

Element type (E)                   ... set of 1..nScs
Specification type (S)             ... bool
Aggregation function : (S^n -> E)  ... link_booleans
Ordering relation R sub E x E      ... is_worse_cr
Best element                       ... {}

*/ 

include "link_set_to_booleans.mzn";
function var set of int: link_invert_booleans(array[int] of var bool: b, 
  par int: nScs, array[int, 1..2] of par int: crEdges, par bool: useSPD) =
let {
  var set of index_set(b): violatedSet;
  constraint link_set_to_booleans(violatedSet, [ not b[i]  | i in index_set(b) ]); 
} in violatedSet;

% gives us access to constraint relationship predicates 
include "soft_constraints/spd_worse.mzn";
include "soft_constraints/tpd_worse.mzn";

predicate is_worse_cr(var set of int: violated1, var set of int: violated2,
                      par int: nScs, array[int, 1..2] of par int: crEdges, par bool: useSPD) =
let {
    par set of int: softConstraints = 1..nScs;
} in (                    
    if useSPD then
      spd_worse(violated1, violated2, softConstraints, crEdges)
    else
      tpd_worse(violated1, violated2, softConstraints, crEdges)
    endif
);

% also a morphism for weightings:
include "soft_constraints/cr_weighting.mzn";
include "soft_constraints/cr_consistency.mzn";

function array[int] of int : calculate_cr_weights(par int: nScs, array[int, 1..2] of par int: crEdges, par bool: useSPD) = 
let {
  % ground penalties using the appropriate weighting
  array[int] of int: penalties = [weighting(s, 1..nScs, crEdges, useSPD) | s in 1..nScs];
} in  penalties;

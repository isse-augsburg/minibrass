
include "free-pvs_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

% the basic, "classic" CSP 
set of int: DOM = 1..4;

function array[int] of var int: embed(var bool: expression, par int: p, par int: maxP) = let 
 {
   set of int: P = 1..maxP;
   array[P] of var 0..1: ret;
   constraint expression -> ret == [0 | p in P];
   constraint not(expression) -> (forall(q in P) ( (ret[q] = 0 /\ p != q) \/ (ret[q] = 1 /\ p = q) ) ); 
 } in ret;

var DOM: x;
%constraint x = 2;

solve 
:: int_search([x], input_order, indomain_min, complete)
search pvs_BAB();

output ["x = \(x);"] ++ [ "\nValuations:  topLevelObjective = \(topLevelObjective):  \( [ sol(topLevelObjective[i]) | i in index_set(topLevelObjective)]))\n"] ++
  [ show(sol(mbr_overall_fp[mbr_iter_var_0])) | mbr_iter_var_0 in 1..mbr_maxP_fp] ;

% Library predicate: 
% -----------------
% Implements single predecessor
% dominance on sets of constraints
% (upper smyth ordering with partial
%  order inversion)
% ---------------------------------
include "alldifferent_except_0.mzn";
include "cr-types.mzn";

predicate spd_worse(var set of int: lhs, var set of int: rhs, 
                     set of int: softConstraints,
                     array[int, 1..2] of int: edges
) = 
let {
  int: le = min(index_set_1of2(edges));
  int: ue = max(index_set_1of2(edges));

  var set of int: lSymDiff = lhs diff rhs;
  var set of int: rSymDiff = rhs diff lhs;
  set of int: softConstraints0 = {0} union softConstraints;
  % 0 represents noVal for constraints not in the right-hand-side

  var set of int: rUndefined = softConstraints diff rSymDiff;

  % I need to make the dominance explicit by a function
  array[softConstraints] of var softConstraints0: witness; 

  % collect all predecessors such that succ in lessThans[pred] 
  % iff succ less than pred
  array[softConstraints] of set of softConstraints: lessThans = 
    [ {succ | succ in softConstraints where exists(e in le..ue) 
         (edges[e,1] = pred /\ edges[e,2] = succ)} | pred in softConstraints];

} in (
  lhs != rhs /\
  alldifferent_except_0(witness) /\  
  forall(s in ub(rUndefined)) (s in rUndefined -> witness[s] = 0) /\ 
  forall(s in ub(rSymDiff)) (s in rSymDiff -> (witness[s] in lSymDiff /\ 
                                               witness[s] in lessThans[s]))    
);

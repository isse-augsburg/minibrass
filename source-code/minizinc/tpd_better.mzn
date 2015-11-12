% Library predicate: 
% -----------------
% Implements transitive predecessor
% dominance on sets of constraints
% ---------------------------------
%include "alldifferent_except_0.mzn";

predicate tpd_better(var set of int: lhs, var set of int: rhs, 
                     set of int: softConstraints,
                     array[int, 1..2] of int: edges
) = 
let {
  int: le = min(index_set_1of2(edges));
  int: ue = max(index_set_1of2(edges));

  var set of int: lSymDiff = lhs diff rhs;
  var set of int: rSymDiff = rhs diff lhs;
  % set of int: softConstraints0 = {0} union softConstraints;
  % 0 represents noVal for constraints not in the right-hand-side

  var set of int: rUndefined = softConstraints diff rSymDiff;

  % I need to make the dominance explicit by a function
  %array[softConstraints] of var softConstraints0: witness; 

  % collect all predecessors such that succ in lessThans[pred] 
  % iff succ less than pred
  array[softConstraints] of set of softConstraints: dominateds = 
    [ {succ | succ in softConstraints where exists(e in le..ue) 
         (edges[e,1] = succ /\ edges[e,2] = pred)} | pred in softConstraints];

  array[softConstraints] of var bool: isDominated = [ exists(d in ub(lSymDiff)) (d in lSymDiff /\ s in dominateds[d])  | s in softConstraints];
  

} in (
  lhs != rhs /\
  forall(r in ub(rSymDiff)) (r in rSymDiff -> isDominated[r])
);

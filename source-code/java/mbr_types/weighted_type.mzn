% Weighted Constraint Type

predicate is_worse_weighted(var int: x, var int: y, 
  par int: nScs, par int: k, array[int] of par int: weights) = 
  x > y;

function var int: weighted_sum(array[int] of var bool: b, 
                               par int: nScs, par int: k, array[int] of par int: weights) =
  sum(i in index_set(b)) ( (1 - bool2int(b[i])) * weights[i]);


function ann: getSearchHeuristicWeighted(set of int: softConstraints, array[int] of var int: degs, var int: overall, 
                                         int: k, array[int] of int: weights) = 
let {
  int: nSoftConstraints = max(softConstraints);

  % find the sorted permutation of soft constraint instances
  array[softConstraints] of softConstraints: sortPermScs = arg_sort(weights);

  % invert, since arg_sort use <= and we need decreasing order
  array[softConstraints] of softConstraints: mostImpFirst = [ sortPermScs[nSoftConstraints-s+1] | s in softConstraints]; 
  array[softConstraints] of var 1..3: mifSatisfied = [ degs[mostImpFirst[s]] | s in softConstraints]; 

} in 
int_search(mifSatisfied, input_order, indomain_max, complete);

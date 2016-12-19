% Probabilistic Constraint Type


predicate is_worse_prob(var float: x, var float: y, par int: nScs, array[int] of par float: probs) = 
  x < y;

function var float: prod(array[int] of var bool: b, 
                     par int: nScs, array[int] of par float: probs) =
  product(i in index_set(b) ) (
      max(bool2int(b[i]) , (1 - probs[i]))
  );
% we take the product of the non-occurrence probabilites for all violated constraints


% Probabilistic Cost Function Networks 
predicate is_worse_prob_cost(var float: x, var float: y, 
  par int: nScs, array[int] of par 0.0 .. 1.0: probs ) = 
  x > y;

function var float: prob_weighted_sum(array[int] of var float: b, 
                               par int: nScs, array[int] of par 0.0 .. 1.0: probs) =
  sum(i in index_set(b)) (probs[i] * b[i]);


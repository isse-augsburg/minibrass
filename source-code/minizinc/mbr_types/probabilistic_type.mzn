% Probabilistic Constraint Type



predicate is_worse_prob(var float: x, var float: y, par int: nScs, array[int] of par float: probs) = 
  x < y;

function var float: prod(array[int] of var bool: b, 
                     par int: nScs, array[int] of par float: probs) =
  product(i in index_set(b) ) (
      max(bool2int(b[i]) , (1 - probs[i]))
  );
% we take the product of the non-occurrence probabilites for all violated constraints

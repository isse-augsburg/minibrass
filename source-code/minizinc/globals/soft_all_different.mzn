%-----------------------------------------------------------------------------%
% Constrains the array of objects 'x' to be all different with a tolerance 
% defined by d. More precisely, a violation measure mu is defined
% that has a constraint return  a distance from a correct solution.
% A soft global constraint (X, c) then holds for a set of variables X
% and cost variable c iff mu(X) <= c. There are two kinds of violation measures
% in the literature:
%     decomposition-based: counts the number of violated constraints in a decomposition
%     variable-based: counts the number of variables that need to be changed to find a valid solution 
%-----------------------------------------------------------------------------%
include "count_fn.mzn";

% http://sofdem.github.io/gccat/gccat/Csoft_alldifferent_ctr.html
function var int: soft_all_different_dec(array[int] of var int: x, var int: cost) 
   :: promise_total = (
        sum(i, j in index_set(x) where i < j) ( bool2int(x[i] = x[j]) ) 
);

% 
% http://sofdem.github.io/gccat/gccat/Csoft_alldifferent_var.html
function var int: soft_all_different_var(array[int] of var int: x, var int: cost) 
   :: promise_total = 
let {
  set of int: seenValues = dom_array (x);
}
in 
(
  sum(s in seenValues) (   max( count(x, s) - 1, 0 ) )
);

predicate soft_all_different(array[int] of var int: x, var int: cost, bool: useDec) = 
let {
  var int: mu = if useDec then 
                   soft_all_different_dec(x, cost)
                else 
                   soft_all_different_var(x, cost)
                endif; 
} 
in 
(
  mu <= cost
);

% just a synonym for the other spelling of alldifferent
predicate soft_alldifferent(array[int] of var int: x, var int: cost, bool: useDec) =
    soft_all_different(x, cost, useDec);

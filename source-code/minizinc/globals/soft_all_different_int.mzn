%-----------------------------------------------------------------------------%
% Constrains the array of objects 'x' to be all different with a tolerance 
% defined by d. More precisely, a violation measure mu is defined
% that has a constraint return  a distance from a correct solution.
% A soft global constraint (X, c) then holds for a set of variables X
% and cost variable c iff mu(X) <= c. There are two kinds of violation measures
% in the literature:
%     decomposition-based: counts the number of violated constraints in a decomposition
%     variable-based: counts the number of variables that need to be changed to find a valid solution 
%
% Consider "Over-Constrained Problems", Willem-Jan van Hoeve, 2010 
%-----------------------------------------------------------------------------%
include "count_fn.mzn";

% Counts the number of binary constraints violated in a decomposition
% \mu_{\textrm{dec}}([x_1, \ldots, x_n]) =  |\{ (i,j) \mid x_i = x_j, i < j \} |
% http://sofdem.github.io/gccat/gccat/Csoft_alldifferent_ctr.html
function var int: soft_all_different_dec(array[int] of var int: x, var int: cost) 
   :: promise_total = (
        sum(i, j in index_set(x) where i < j) ( bool2int(x[i] = x[j]) ) 
);

% Counts the number of variables that need to change for a correct solution
%   \mu_{\textrm{var}}([x_1, \ldots, x_n]) =  \sum_{d \in D(X)} \max (|\{i \mid x_i = d\}|-1, 0)
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

predicate soft_all_different_int(array[int] of var int: x, var int: cost, bool: useDec) = 
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


include "link_set_to_booleans.mzn";

% number of soft constraints 
int: nScs; 
set of int: SOFTCONSTRAINTS = 1..nScs;

% reified variables for each soft constraint
array[SOFTCONSTRAINTS] of var bool: satisfied;
array[SOFTCONSTRAINTS] of var bool: violated = [not satisfied[sc] | sc in SOFTCONSTRAINTS] ;

array[SOFTCONSTRAINTS] of int: penalties;
var int: penSum;
constraint penSum = sum(sc in SOFTCONSTRAINTS) (bool2int(not satisfied[sc]) * penalties[sc]);

% find the sorted permutation of soft constraint instances
array[SOFTCONSTRAINTS] of SOFTCONSTRAINTS: sortPermScs = arg_sort(penalties);
% invert, since arg_sort use <= and we need decreasing order
array[SOFTCONSTRAINTS] of SOFTCONSTRAINTS: mostImpFirst = [ sortPermScs[nScs-s+1] | s in SOFTCONSTRAINTS]; 

array[SOFTCONSTRAINTS] of var bool: mifSatisfied = [ satisfied[mostImpFirst[s]] | s in SOFTCONSTRAINTS]; 
ann: mifAnn = int_search(mifSatisfied, input_order, indomain_max, complete);

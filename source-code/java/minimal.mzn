% Generated .mzn file: SAMPLE-PROB
% --------------------------------------- 
include "table.mzn";

set of int: DOM  = 0..2;
set of int: VARS = 0..2;
int: nCfns = 3; % number of cost function networks
set of int: CFNS = 1..nCfns;
array[VARS] of var DOM: x;
array[CFNS] of var bool: v; % violated or not 
array[CFNS] of int: cost;
int: sumCost;
var 0..sumCost: OBJ;
constraint OBJ = sum(i in CFNS) (bool2int(v[i])*cost[i] );
constraint OBJ <= 4;

% find the sorted permutation of soft constraint instances
array[CFNS] of CFNS: sortPermScs = arg_sort(cost);
% invert, since arg_sort use <= and we need decreasing order
array[CFNS] of CFNS: mostImpFirst = [ sortPermScs[nCfns-s+1] | s in CFNS]; 

constraint x[0] < 3;
constraint x[1] < 3;
constraint x[2] < 3;

constraint table([x[0], x[1]], [| 0, 1
 | 1, 2
 |])  <-> v[1] ;
constraint table([x[1], x[2]], [| 0, 2
 |])  <-> v[2] ;
constraint table([x[0], x[1]], [| 1, 1
 | 0, 2
 | 1, 0
 | 0, 1
 | 0, 0
 | 2, 0
 |])  <-> v[3] ;
sumCost = 4;
cost = [2, 1, 1];
solve 
:: seq_search([  
              int_search([ v[mostImpFirst[c]] | c in CFNS], input_order, indomain_min, complete), 
              int_search(x, dom_w_deg, indomain_min, complete) 
              ]) 
minimize OBJ; 

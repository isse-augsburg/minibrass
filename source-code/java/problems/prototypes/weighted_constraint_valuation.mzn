% simple prototype for an inclusion-based PVS
var 0..1: x; 
var 0..1: y;
var 0..1: z;
% this and other formalisms purely 
% rely on the intuition of "soft constraints"
% being either true or false and a resulting mapping
% from it 

% cost functions 
/* 
f_1  (x_or) weight 3
x y | 
0 0 | 0 (3)
0 1 | 1 
1 0 | 1 
1 1 | 0 (3)

f_2  (!y and !z) weight 2 
y z |
0 0 | 1 
0 1 | 0 (2)
1 0 | 0 (2)
1 1 | 0 (2)

f_3 weight 1 
y |  ! y
0 | 1
1 | 0 (1)
 
*/
% type of PVS: int, best: 0, order >=, combination sum

int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs; 
set of SOFTCONSTRAINTS: bound = SOFTCONSTRAINTS;

% valuation set
 
% variables for valuations 
array[SOFTCONSTRAINTS] of var 0..8: scValuations;

% maybe easier for specification
array[SOFTCONSTRAINTS] of var bool: scSatisfied;
array[SOFTCONSTRAINTS] of var bool: scViolated = [not scSatisfied[sc] | sc in SOFTCONSTRAINTS];

constraint forall(sc in SOFTCONSTRAINTS) (scSatisfied[sc] <-> scValuations[sc] = {});
include "link_set_to_booleans.mzn";

var set of SOFTCONSTRAINTS: overall;
constraint link_set_to_booleans(overall,scViolated); 

% here: tie scValuations to set in the ever-same fashion
constraint forall(sc in SOFTCONSTRAINTS) (
  scValuations[sc] = if scSatisfied[sc] then {} else {sc} endif
);

constraint scSatisfied[1] <-> (x == 1 xor y == 1);
constraint scSatisfied[2] <-> (y = 0 /\  z = 0); 
constraint scSatisfied[3] <-> y == 0; 

array[SOFTCONSTRAINTS] of int: weights = [3, 2, 1];
int: k = sum(weights);
set of int: VAL = 0..k;

function var VAL: objective(var set of SOFTCONSTRAINTS: violatedScs) = (sum(i in violatedScs) (weights[i]) );
% function var VAL: objective(var set of SOFTCONSTRAINTS: violatedScs) = (max(i in violatedScs) (weights[i]) ); % worst case 
% function var VAL: objective(var set of SOFTCONSTRAINTS: violatedScs) = (min(i in violatedScs) (weights[i]) ); % best case 
var VAL: obj;
constraint obj = objective(overall);

solve minimize obj;
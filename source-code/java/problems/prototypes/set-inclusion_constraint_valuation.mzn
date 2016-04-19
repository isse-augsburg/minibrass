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
f_1  (x_or)
x y | 
0 0 | 0 
0 1 | 1 
1 0 | 1
1 1 | 0

f_2  (!y and !z)
y z |
0 0 | 1 
0 1 | 0
1 0 | 0
1 1 | 0

f_3
y |  ! y
0 | 1
1 | 0

some intensional form
f_3 = 1 - y 
*/
% type of PVS: int, best: 0, order >=, combination sum

int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs; 
set of SOFTCONSTRAINTS: bound = SOFTCONSTRAINTS;

% valuation set
 
% variables for valuations 
array[SOFTCONSTRAINTS] of var set of SOFTCONSTRAINTS: scValuations;

% maybe easier for specification
array[SOFTCONSTRAINTS] of var bool: scSatisfied;

array[SOFTCONSTRAINTS] of var bool: scViolated = [not scSatisfied[sc] | sc in SOFTCONSTRAINTS];

constraint forall(sc in SOFTCONSTRAINTS) (scSatisfied[sc] <-> scValuations[sc] = {});

include "link_set_to_booleans.mzn";

function var set of int: link_booleans(array[int] of var bool: b) =
let {
  
  var set of index_set(b): resultSet;
  constraint link_set_to_booleans(resultSet,b); 
} in resultSet;

var set of SOFTCONSTRAINTS: overall = link_booleans(scViolated);

% here: tie scValuations to set in the ever-same fashion
constraint forall(sc in SOFTCONSTRAINTS) (
  scValuations[sc] = if scSatisfied[sc] then {} else {sc} endif
);

constraint scSatisfied[1] = {} <-> (x == 1 xor y == 1);
% scSatisfied[1] in { {}, {1} } 
constraint scSatisfied[2] <-> (y = 0 /\  z = 0); 
constraint scSatisfied[3] <-> y == 0; 

solve minimize card(overall);
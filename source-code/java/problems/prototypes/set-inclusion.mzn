% simple prototype for an inclusion-based PVS
var 0..1: x; 
var 0..1: y;
var 0..1: z;

% cost functions 
/* 
f_1  (x_or)
x y | 
0 0 | 4 0 
0 1 | 3 1 
1 0 | 2 1
1 1 | 3 0

f_2  (!y and !z)
y z |
0 0 | 2 1 
0 1 | 5 0
1 0 | 6 0
1 1 | 7 0

f_3
y |  ! y
0 | 3 1
1 | 8 0

some intensional form
f_3 = 3 + y * 5
*/
% type of PVS: int, best: 0, order >=, combination sum

int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs; 
set of SOFTCONSTRAINTS: bound = SOFTCONSTRAINTS;

% valuation set
 
% variables for valuations 
array[SOFTCONSTRAINTS] of var set of SOFTCONSTRAINTS: scValuations;

% here: tie scValuations (functionally!) to other model variables ...
% this could be generated:
constraint scValuations[1] = f_1();
constraint scValuations[2] = f_2(y,z);
constraint scValuations[3] = f_3();

include "table.mzn";

% utility for embedding
function var set of SOFTCONSTRAINTS: asSoftConstraint(var bool: condition, SOFTCONSTRAINTS: ident) = 
if condition then {} else {ident} endif;

% the extensional form - less cluttered, based on global variables
function var set of SOFTCONSTRAINTS: f_1() = asSoftConstraint(x = 1 xor y = 1, 1);

% another extensional form - with formal parameters (maybe nicer to see the scope) 
function var set of SOFTCONSTRAINTS: f_2(var int: x_, var int: y_) = 
  asSoftConstraint(x_ = 0 /\  y_ = 0, 2);

% the intensional form 
function var set of SOFTCONSTRAINTS: f_3() = asSoftConstraint(y == 0, 3);

var set of SOFTCONSTRAINTS: overall = array_union(scValuations);

solve minimize card(overall);
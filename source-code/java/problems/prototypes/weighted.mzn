% simple prototype for a classical weighted csp
var 0..1: x; 
var 0..1: y;
var 0..1: z;

% cost functions 
/* 
f_1 
x y | 
0 0 | 4
0 1 | 3 
1 0 | 2 
1 1 | 3
[| 0, 0, 4 | 0, 1, 3 | 1, 0, 2 | 1, 1, 3|] ; 

f_2
y z |
0 0 | 2 
0 1 | 5
1 0 | 6
1 1 | 7

f_3
y |
0 | 3
1 | 8

some intensional form
f_3 = 3 + y * 5
*/
% type of PVS: int, best: 0, order >=, combination sum
int: bound = 8;
int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs; 

% valuation set
set of int: VAL = 0..bound; 
% variables for valuations 
array[SOFTCONSTRAINTS] of var VAL: scValuations;

% here: tie scValuations (functionally!) to other model variables ...
% this could be generated:
constraint scValuations[1] = f_1();
constraint scValuations[2] = f_2(x,y);
constraint scValuations[3] = f_3();

include "table.mzn";

% the extensional form - less cluttered, based on global variables
function var VAL: f_1() = 
let {
  array[int, 1..3] of int: costFunc = [| 0, 0, 4 | 0, 1, 3 | 1, 0, 2 | 1, 1, 3|] ;
  var VAL: value;
  constraint table([x,y,value], costFunc);
} in ( value );

% another extensional form - with formal parameters (maybe nicer to see the scope) 
function var VAL: f_2(var int: x_, var int: y_) = 
let {
  array[int, 1..3] of int: costFunc = [| 0, 0, 4 | 0, 1, 3 | 1, 0, 2 | 1, 1, 3|] ;
  var VAL: value;
  constraint table([x_,y_,value], costFunc);
} in ( value );

% the intensional form 
function var VAL: f_3() = ( 3 + y * 5);

var VAL: overall = sum(i in SOFTCONSTRAINTS) (scValuations[i]);

solve minimize overall;
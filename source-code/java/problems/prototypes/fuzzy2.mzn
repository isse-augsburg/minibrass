% simple prototype for a classical fuzzy csp
% but this time use *rational* numbers
var 0..1: x; 
var 0..1: y;
var 0..1: z;

include "soft_constraints/fuzzy_encoding.mzn";
% cost functions 
/* 
f_1 
x y | 
0 0 | 1 (0.23)
0 1 | 2 (0.45) 
1 0 | 3 (0.8)
1 1 | 4 (0.48)
[0.23, 0.45, 0.8, 0.48]

[| 0, 0, 0.23 | 0, 1, 0.45 | 1, 0, 0.8 | 1, 1, 0.48|] ; 

f_2
y z |
0 0 | 2 (0.43)
0 1 | 5 (0.78)
1 0 | 6 (0.80)
1 1 | 7 (0.4)
[0.43, 0.78, 0.8, 0.4]

f_3
y |
0 | 3 (0.2)
1 | 8 (0.54)

some intensional form
f_3 = 3 + y * 5
*/
% type of PVS: int, best: 0, order >=, combination sum
int: bound = 8;
int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs; 

% valuation set
set of int: VAL = 0..bound; set of float: FVAL = 0.0 .. 1.0;
% variables for valuations 
array[SOFTCONSTRAINTS] of var FVAL: scValuations;

% here: tie scValuations (functionally!) to other model variables ...
% this could be generated:
constraint scValuations[1] = f_1();
constraint scValuations[2] = f_2(y,z);
constraint scValuations[3] = f_3();


% the extensional form - less cluttered, based on global variables
% [0.23, 0.45, 0.8, 0.48]
function var FVAL: f_1() = 
let {
  array[int] of int: satDegrees =  [23, 45, 80, 48] ;
  var FVAL: value; 
  constraint binary_fuzzy_rational(satDegrees, 100, x, y, value);
} in ( value );

% another extensional form - with formal parameters (maybe nicer to see the scope) 
function var FVAL: f_2(var int: x_, var int: y_) = 
let {
  array[int] of int: satDegrees = [43, 78, 80, 40] ;
  var FVAL: value; 
  constraint binary_fuzzy_rational(satDegrees, 100, x_, y_, value);
} in ( value );

% the intensional form 
function var FVAL: f_3() = ( if y = 0 then 0.2 else 0.54 endif);

var FVAL: overall;
constraint overall = min(scValuations);

solve maximize overall;
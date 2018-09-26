include "globals.mzn";

set of int: DOM = 0..2;

var DOM: a;
var DOM: b;
var DOM: c;

constraint alldifferent([a,b,c]);

array[1..2,1..2] of var 1..1: arrayType;
var set of 1..1: setType;
array[1..2] of var set of 1..1: arrayOfSetType;
array[1..2,1..2] of var set of 1..1: array2dOfSetType;

constraint c = 2;
constraint a = 0;
constraint b = 1;
constraint setType = {};
constraint arrayOfSetType = array1d(1..2, [{}, {}]);
constraint array2dOfSetType = array2d(1..2, 1..2, [{}, {}, {}, {}]);

output ["MyOutput: A = \(a), B = \(b), C = \(c)"];
solve satisfy;
include "globals.mzn";

set of int: DOM = 0..2;

var DOM: a;
var DOM: b;
var DOM: c;

constraint alldifferent([a,b,c]);

array[0..2] of var 1..1: array1dType;
array[1..2,1..2] of var 1..1: arrayType;
var set of 1..1: setType;
var set of 1..2: set2Type;
array[1..2] of var set of 1..1: arrayOfSetType;
array[1..2,1..2] of var set of 1..1: array2dOfSetType;
var 0.0 .. 1.0: floatType;
var 0.0 .. 1.0: float2Type;

constraint c = 2;
constraint a = 0;
constraint b = 1;
constraint floatType = 0.5;
constraint float2Type = 0;
constraint setType = {};
constraint set2Type = {1,2};
constraint arrayOfSetType = array1d(1..2, [{}, {}]);
%constraint array1dType = [1,1,1];
constraint array2dOfSetType = array2d(1..2, 1..2, [{}, {}, {}, {}]);

% output ["MyOutput: A = \(a), B = \(b), C = \(c)"];
solve satisfy;

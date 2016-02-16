% toy packing to investigate performance
int: n = 4  ; % number of squares
set of int: SQUARE = 1..n;
int: maxl = sum(i in SQUARE) (i);
int: mina = sum(i in SQUARE) (i*i);
var n..maxl: height;
var n..maxl: width;

var mina .. n*maxl: area = height*width;
array[SQUARE] of var 0..maxl: x;
array[SQUARE] of var 0..maxl: y;

% let the squares fit in the rectangle 
constraint forall(s in SQUARE) (x[s] + s <= width);
constraint forall(s in SQUARE) (y[s] + s <= height);

% now the non-overlaps (globals!)
include "diffn.mzn";

/*
predicate diffn(array [int] of var int: x, 
                array [int] of var int: y, 
                array [int] of var int: dx, 
                array [int] of var int: dy)
*/
constraint 
let 
{
  array[SQUARE] of SQUARE: lens = [ s | s in SQUARE ];
 }
in ( diffn(x,y, lens, lens));

% variable ordering -- biggest objects first 
array[1..2*n] of var 0..maxl: vs = [ 
  if i mod 2 = 1 then x[n+1 - i div 2]
  else y[n+1 - i div 2]
  endif 
| i in 2..2*n+1
];

% redundant cumulative constraints:
include "cumulative.mzn";
array[SQUARE] of SQUARE: lens = [ s | s in SQUARE ];
constraint cumulative(x, lens, lens, height); 
constraint cumulative(y, lens, lens, width);

% now some soft constraint business here:
include "soft_constraints/soft_alldifferent.mzn";

constraint soft_alldifferent(y, 5, true);

solve :: seq_search ( [ 
            int_search( [area, width, height], input_order, indomain_min, complete),
            int_search(vs, input_order, indomain_min, complete)
 ])
minimize area;
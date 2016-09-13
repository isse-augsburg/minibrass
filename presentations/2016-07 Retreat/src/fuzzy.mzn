% now a very simple fuzzy csp

var 0..1: x;
var 0..1: y;
var 0..1: z; 

% fuzzy table - two fuzzy constraints 
/* x y val 
   0 0 0.2 (1
   0 1 0.3 (2
   1 0 0.2 (3
   1 1 0.4 (4

   x z val
   0 0 0.8 (1
   0 1 0.6 (2
   1 0 0.7 (3
   1 1 0.3 (4 
*/

include "table.mzn";
include "globals.mzn";

int: maxSize = 10;

int: nScs = 2;
array[1..nScs] of var float: degrees;

array[1..nScs] of var 1..maxSize: symDegree;
array[1..nScs, int] of float: translated = [| 0.2, 0.3, 0.2, 0.4 | 0.8, 0.6, 0.7, 0.3 |]; 

constraint table( [x,y,symDegree[1]], [| 0, 0, 1 | 0, 1, 2 | 1, 0, 3 | 1, 1, 4 |] );
constraint table( [x,y,symDegree[2]], [| 0, 0, 1 | 0, 1, 2 | 1, 0, 3 | 1, 1, 4 |] );

constraint forall(i in 1..nScs) ( degrees[i] = translated[i, symDegree[i]] );  

function var float: minimum(array[int] of var float: vals) =
let {
  var float: obj;
  constraint minimum(obj, vals);
} in (obj);

function var float: maximum(array[int] of var float: vals) =
let {
  var float: obj;
  constraint maximum(obj, vals);
} in (obj);

function var float: sum(array[int] of var float: vals) =
let {
  var float: obj;
  constraint obj = sum(i in index_set(vals)) (vals[i]);
} in (obj);


var float: obj = maximum(degrees);
solve maximize obj;
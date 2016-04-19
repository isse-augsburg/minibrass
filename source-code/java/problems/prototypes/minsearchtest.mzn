% minisearch small
include "minisearch.mzn";

var 0..1: x;
var 0..1: y;
var int: obj;
constraint x + y = obj;

function ann: maximize_bab_int(var int: obj) = 
   repeat(
       if next() then
         commit() /\ 
         post(obj > sol(obj))
       else break endif
   );
solve
search maximize_bab_int(obj) /\ print();
%solve maximize obj;
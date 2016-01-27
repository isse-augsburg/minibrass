% now just an experimental lns with only predefined variables
include "minisearch.mzn";
array[1..10] of var 0..10: xs;
constraint xs[2] <= 8;
% doesn't work:
%var 0..20: x3 = 2*xs[2];

% works
var 0..20: x3;
constraint x3 = 2*xs[2];

var 0..18: obj;

constraint obj = xs[1] + xs[2];

constraint xs[1] < xs[2];

function ann: primTest (var int: x3) = 
    
     next() /\ commit() /\ post(x3 = sol(x3)) /\ print("Found 1 solution and posted")
;

solve 

search primTest(x3) /\ if hasSol() then print() else print("No solution found\n") endif;


include "minisearch.mzn";

var 0..3: x;

% doesn't work:
%var 0..6: y = 2*x;

% works
var 0..6: y;
constraint y = 2*x;

function ann: minSearch (var int: y) = 
     next() /\ commit() /\ 
     post(y = sol(y)) /\ print("Found 1 solution and posted")
;

solve 

search minSearch(y);


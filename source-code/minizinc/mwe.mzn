% a very minimal example to provoke iptr bug
% ---------------------------------
include "minisearch.mzn"; % include the search minisearch lite
var 0..5: x;

% custom 'less than' "implementation" as predicate
predicate lessThan(var int: x, var int: y) = 
let {
  var int: dif;
  constraint dif > 0;
} in
(
  x + dif = y
);


function ann: mybab(var int: obj) =
       repeat(
           if next() then
               let {
                 int: ub = sol(obj); 
               } in (
                 print("Violated scs now: \(ub)\n") /\
                 commit() /\  post(lessThan(obj,ub)) % post(obj < ub)
               )
           else break endif
       );
       
solve
:: int_search([x], input_order, indomain_max, complete)
%search time_limit(3000, mybab(x)) /\ if hasSol() then print() else print("No solution found\n") endif;
search mybab(x) /\ if hasSol() then print() else print("No solution found\n") endif;


%minimize x;


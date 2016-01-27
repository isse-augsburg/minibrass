% a very minimal example to provoke isa: nullptr bug
% ---------------------------------
include "minisearch.mzn"; % include the search minisearch lite

set of int: range = 1..5;
var set of range: x;

predicate onlyEvens(var set of range: y) = 
(
 forall(r in range) (r in y -> r mod 2 = 0)
);


function ann: mybab(var set of int: obj) =
       repeat(
           if next() then
               let {
                 set of range: ub = sol(obj); 
               } in (
                 print("Violated scs now: \(ub)\n") /\
                 commit() /\ post(onlyEvens(obj))
               )
           else break endif
       );
       
solve
:: set_search([x], input_order, indomain_max, complete)
search mybab(x) /\ if hasSol() then print() else print("No solution found\n") endif;


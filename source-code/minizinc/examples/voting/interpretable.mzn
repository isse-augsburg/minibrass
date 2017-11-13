% voting 2
include "minisearch.mzn";

var 1..3: a;
predicate isWorse(var 1..3: left, var 1..3: right) =
left < right;
 
function ann: postGetBetter(var bool: aLikes) = 
  post(
   isWorse(sol(a), a) /\ aLikes = false
  );
  
function ann: pvs_BAB() =
       repeat(
           if next() then 
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ 
                 scope (
                   let {
                     var bool: aLovesThat;
                   } in
                   postGetBetter(aLovesThat)
                 )
           else break endif
       );


solve
:: int_search([a], input_order, indomain_min, complete)
 search pvs_BAB();

output["a = \(a)"];

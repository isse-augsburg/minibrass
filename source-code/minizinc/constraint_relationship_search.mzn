% PVS-specific search strategies
% ---------------------------------------------------

% only declare predicate for worsening
predicate isWorse(var set of int: leftViolatedScs, var set of int: rightViolatedScs); 

function ann: strictlyBetterBAB(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
               let {
                 set of SOFTCONSTRAINTS: lb = sol(violatedScs); 
               } in (
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ post(isWorse(lb, violatedScs))
               )
           else break endif
       );
       
function ann: onlyNotDominatedBAB(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then
               let {
                 set of SOFTCONSTRAINTS: lb = sol(violatedScs); 
               } in (
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ post(not (isWorse(violatedScs, lb) \/ violatedScs = lb ) )
               )
           else break endif
       );
       
% adapted from lns_max an objective value (the set of violated constraints towards {}, in our case)
function ann: lns_pvs (var set of SOFTCONSTRAINTS: violatedScs, array[int] of var int: x,
                   int: iterations, float: d) = 
    repeat (i in 1..iterations) (
        scope(
            post(neighbourhoodCts(x,d)) /\
            next() /\ %bab_min(obj) /\ % strictlyBetterBAB(obj)
            commit() /\ 
            print("Intermediate solution with objective \(sol(violatedScs))\n") /\
            print()
        ) /\
        let { set of SOFTCONSTRAINTS: lb = sol(violatedScs); } in 
        ( post(
           isWorse(lb, violatedScs) 
          ) 
        )
   );


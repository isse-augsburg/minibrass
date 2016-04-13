% PVS-specific search strategies
% ---------------------------------------------------

include "minisearch.mzn";
include "soft_constraints/pvs_set_based.mzn";

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
                   int: iterations, float: d, int: exploreTime) = 
    repeat (i in 1..iterations) (
        print("Starting iteration ... \(i)\n") /\
        scope(
            post(neighbourhoodCts(x,d)) /\
            time_limit(exploreTime,
                strictlyBetterBAB(violatedScs)) /\
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
   
   
function ann: adaptive_lns_pvs(var set of SOFTCONSTRAINTS: violatedScs, array[int] of var int: vars,
                        int: iterations, int: initRate, int: exploreTime) =
  let { int: adaRate = initRate, int: step = 1; } in
  repeat (i in 1..iterations) (
    print("Starting iteration ... \(i)\n") /\
    scope(
         (post(uniformNeighbourhood(vars,adaRate/100.0)) /\
          time_limit(exploreTime,
                strictlyBetterBAB(violatedScs)) /\ 
          commit() /\ 
          print("Intermediate solution with objective \(sol(violatedScs))\n") /\
          print() )
      \/ (adaRate := adaRate + step /\ fail)
    )
    /\ 
    let { set of SOFTCONSTRAINTS: lb = sol(violatedScs); } in 
        ( post(
           isWorse(lb, violatedScs) 
          ) 
        )
  );


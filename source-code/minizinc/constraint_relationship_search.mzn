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
       
function ann: onlyNotDominated(var set of SOFTCONSTRAINTS: violatedScs) =
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


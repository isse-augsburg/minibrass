% Constraint-relationship-specific search strategies
% ---------------------------------------------------

function ann: strictlyBetterBAB(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
               let {
                 set of SOFTCONSTRAINTS: ub = sol(violatedScs); 
               } in (
                 print("Violated scs now (penalty: \(sol(penSum)): \(ub) x -> \(sol(x)), y -> \(sol(y)), z -> \(sol(z))\n") /\
                 commit() /\ post(spd_better(ub, violatedScs, SOFTCONSTRAINTS, edges))
               )
           else break endif
       );
       
function ann: onlyNotDominated(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then
               let {
                 set of SOFTCONSTRAINTS: ub = sol(violatedScs); 
               } in (
                 print("Violated scs now (penalty: \(sol(penSum)): \(ub) x -> \(sol(x)), y -> \(sol(y)), z -> \(sol(z))\n") /\
                 commit() /\ post(not (spd_better(violatedScs, ub, SOFTCONSTRAINTS, edges) \/ violatedScs = ub ) )
               )
           else break endif
       );

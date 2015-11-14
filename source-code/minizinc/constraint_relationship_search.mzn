% Constraint-relationship-specific search strategies
% ---------------------------------------------------

function ann: strictlyBetterBAB(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
               let {
                 set of SOFTCONSTRAINTS: lb = sol(violatedScs); 
               } in (
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ post(spd_better(lb, violatedScs, SOFTCONSTRAINTS, edges))
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
                 commit() /\ post(not (spd_better(violatedScs, lb, SOFTCONSTRAINTS, edges) \/ violatedScs = lb ) )
               )
           else break endif
       );

function ann: strictlyBetterBAB_TPD(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
               let {
                 set of SOFTCONSTRAINTS: ub = sol(violatedScs); 
               } in (
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ post(tpd_better(ub, violatedScs, SOFTCONSTRAINTS, edges))
               )
           else break endif
       );
       
function ann: onlyNotDominated_TPD(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
           if next() then
               let {
                 set of SOFTCONSTRAINTS: ub = sol(violatedScs); 
               } in (
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ post(not (tpd_better(violatedScs, ub, SOFTCONSTRAINTS, edges) \/ violatedScs = ub ) )
               )
           else break endif
       );

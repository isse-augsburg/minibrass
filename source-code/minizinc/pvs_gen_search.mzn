% Generic PVS-specific search strategies
% only requires a predicate:
%   predicate getBetter()
% ---------------------------------------------------

include "minisearch.mzn";

% only declare minisearch function that posts a constraint which solution has to surpassed
function ann: postGetBetter(); % = post(getBetter2(sol(x)));

function ann: pvs_BAB() =
       repeat(
           if next() then %:: int_search(satisfied, input_order, indomain_min, complete) then
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ postGetBetter()
           else break endif
       );
       
       
% adapted from lns_max an objective value (the set of violated constraints towards {}, in our case)
function ann: pvs_LNS (array[int] of var int: x,
                   int: iterations, float: d, int: exploreTime) = 
    repeat (i in 1..iterations) (
        print("Starting iteration ... \(i)\n") /\
        scope(
            post(neighbourhoodCts(x,d)) /\
            time_limit(exploreTime,
                pvs_BAB()) /\
            commit() /\ 
            print("Intermediate solution\n") /\
            print()
        ) /\ postGetBetter() 
   );
   
   
function ann: pvs_adaptive_LNS(array[int] of var int: vars,
                        int: iterations, int: initRate, int: exploreTime) =
  let { int: adaRate = initRate, int: step = 1; } in
  repeat (i in 1..iterations) (
    print("Starting iteration ... \(i)\n") /\
    scope(
         (post(uniformNeighbourhood(vars,adaRate/100.0)) /\
          time_limit(exploreTime,
                pvs_BAB()) /\ 
          commit() /\ 
          print("Intermediate solution\n") /\
          print() )
      \/ (adaRate := adaRate + step /\ fail)
    )
    /\ postGetBetter()     
  );


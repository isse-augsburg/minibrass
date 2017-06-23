% Generic PVS-specific search strategies
% only requires a predicate:
%   predicate getBetter()
% ---------------------------------------------------

include "minisearch.mzn";

% only declare minisearch function that posts a constraint which solution has to surpassed
function ann: postGetBetter(); 
function ann: postNotGetWorse();

% Classical branch-and-bound search based on PVS.
% Requires every solution to be *better* than the previous one
function ann: pvs_BAB() =
       repeat(
           if next() then 
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ postGetBetter()
           else break endif
       );
       
% Derived branch-and-bound search based on PVS.
% Requires every solution not to be *dominated* by any previous one       
function ann: pvs_BAB_NonDom() =
       repeat(
           if next() then
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ postNotGetWorse()
           else break endif
       );
       
              
% adapted from lns_max an objective value
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


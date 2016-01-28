% 
% n queens problem in MiniZinc.
% 
% Using alldifferent. 
%
% 
% This MiniZinc model was originally created by Hakan Kjellerstrand, hakank@bonetmail.com
% See also my MiniZinc page: http://www.hakank.org/minizinc
%
include "globals.mzn";

include "minisearch.mzn"; % include the search minisearch lite
include "soft_constraints/soft_constraints.mzn"; % model additions for soft constraint business
include "soft_constraints/spd_worse.mzn"; % the actual isBetter predicate
include "soft_constraints/tpd_worse.mzn"; % the actual isBetter predicate
include "soft_constraints/pvs_search.mzn"; % search strategies for SPD better
include "soft_constraints/pvs_tpd.mzn";

int: n;
array[1..n] of var 1..n: queens;



solve :: 
       seq_search([
         int_search( [ satisfied[mostImpFirst[i]] | i in SOFTCONSTRAINTS] , input_order, indomain_max, complete),
         int_search(
           queens, 
           first_fail,
           indomain_median,
           complete
       )] ) 
search time_limit(3000, strictlyBetterBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;

constraint all_different(queens); 
constraint all_different([queens[i]+i | i in 1..n]); 
constraint all_different([queens[i]-i | i in 1..n]); 

% soft constraints 

% it should be desirable to have queen[n/2] be in pos n/2
nScs = 3;
constraint queens[n div 2] = n div 2 <-> satisfied[1] ; 
constraint queens[2] = queens[1] + 2 <-> satisfied[2];
constraint queens[3] = queens[2] + 2 <-> satisfied[3];

output 
[
  show(violatedScs) ++ "; "
]
++
[
  show(queens)
] 
++ 
[
  if j = 1 then "\n" else "" endif ++
     if fix(queens[i]) = j then          
        % show_int(2,j)
        "Q"
     else
        "#"
     endif
  | i in 1..n, j in 1..n
] ++
["\n"]
;

n = 8;

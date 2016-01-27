% 
% n queens problem in MiniZinc.
% 
% Using alldifferent. 
%
% 
% This MiniZinc model was created by Hakan Kjellerstrand, hakank@bonetmail.com
% See also my MiniZinc page: http://www.hakank.org/minizinc
%
include "globals.mzn";

include "minisearch.mzn"; % include the search minisearch lite
include "../soft_constraints.mzn"; % model additions for soft constraint business
include "../spd_worse.mzn"; % the actual isBetter predicate
include "../tpd_worse.mzn"; % the actual isBetter predicate
include "../constraint_relationship_search.mzn"; % search strategies for SPD better
include "../pvs-tpd.mzn";

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
%     satisfy;
%search time_limit(3000, strictlyBetterBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;
search time_limit(3000, onlyNotDominatedBAB(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;

% solve satisfy;

% Chuffed (with --free) seems to perform better without :: domain
% on the first constraint.
constraint all_different(queens); % :: domain;
constraint all_different([queens[i]+i | i in 1..n]); % :: domain;
constraint all_different([queens[i]-i | i in 1..n]); % :: domain;

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

% data
n = 8;
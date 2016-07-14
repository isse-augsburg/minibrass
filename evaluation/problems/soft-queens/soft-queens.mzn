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
include "evaluation-config.mzn";

include "soft_constraints/soft_constraints.mzn"; % model additions for soft constraint business
% include PVS-specific libraries 

include "evaluation-pvs.mzn";

int: n;
array[1..n] of var 1..n: queens;

array[int] of var int: lnsVars = queens;
ann: custSearch = int_search(
           queens, 
           first_fail,
           indomain_median,
           complete
       );
include "evaluation-heuristics.mzn";

include "evaluation-search.mzn";

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
  "\npenSum = \(penSum)\n"++
  show(violatedScs) ++  " (\(penSum)) " ++ " ; "
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


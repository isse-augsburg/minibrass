% job shop scheduling
% n jobs, sequence of m tasks
int: n; set of int: JOB = 1..n;
int: m; set of int: MACH = 1..m;

set of int: TASK = 1..m;
array[JOB,TASK] of int:  du; % duration
array[JOB,TASK] of MACH: mc; % machine to schedule task on
int: maxHorizon = sum(j in JOB, t in TASK) (du[j,t]);
set of int: HORIZON = 0..maxHorizon;

% now the actual decisions 
array[JOB, TASK] of var HORIZON: s;
array[JOB, TASK] of var HORIZON: e;

constraint forall(j in JOB, t in TASK) (s[j,t] + du[j,t] = e[j,t]);

predicate nonoverlap(var HORIZON: s1, var HORIZON: e1,
                     var HORIZON: s2, var HORIZON: e2) = 
           e1 <= s2 \/ e2 <= s1;
% precedences
constraint forall(j in JOB,t in 1..m-1) (
   s[j,t+1] >= e[j,t]
) ;

% non-overlaps 
constraint forall(j1,j2 in JOB, t1, t2 in TASK where j1 < j2 /\ mc[j1,t1] = mc[j2,t2])
                 (nonoverlap(s[j1,t1], e[j1,t1], s[j2,t2], e[j2,t2]));

% now try using disjunctive 
/*
/** @group globals.scheduling
  Requires that a set of tasks given by start times \a s and durations \a d
  do not overlap in time. Tasks with duration 0 can be scheduled at any time,
  even in the middle of other tasks.

  Assumptions:
  - forall \p i, \a d[\p i] >= 0
predicate disjunctive(array[int] of var int: s,
                      array[int] of var int: d)
*/
/*
include "disjunctive.mzn";
constraint forall(m in MACH) 
(let {
  % collect the start times of all tasks on the same machine
  array[int] of var HORIZON: ss = [s[j,t] |j in JOB, t in TASK where mc[j,t] = m];
  % as well as the durations of all tasks on the same machine
  array[int] of var HORIZON: dd = [du[j,t] |j in JOB, t in TASK where mc[j,t] = m];
}
in 
(
   disjunctive(ss,dd)
));

*/ 

% makespan
var HORIZON: makeSpan;
constraint forall(j in JOB) (e[j,m] <= makeSpan);

solve minimize makeSpan;
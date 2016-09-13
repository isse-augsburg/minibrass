int: T; set of int: TASK = 1..T;

include "disjunctive.mzn";
set of int: HORIZON = 0..50;
array[TASK] of var HORIZON: s; % start times
array[TASK] of var HORIZON: e; % end times 
var HORIZON: makeSpan; 

% just identifiers
T = 5;
int: readAlfred = 1; int: codeAlfred = 2;
int: readBen = 3; int: codeBen = 4;
int: workMother = 5;

% durations
array[TASK] of int: d = [5, 2, 3, 8, 12];

constraint forall(t in TASK) (s[t] + d[t] = e[t]);
constraint forall(t in TASK) (e[t] <= makeSpan);

% precedences
constraint e[readAlfred] <= s[codeAlfred] /\ e[readBen] <= s[codeBen];

set of TASK: conflicting = {codeAlfred, codeBen, workMother};

constraint disjunctive( [s[t] | t in conflicting], [d[t] | t in conflicting] ); 
constraint nonoverlap(codeAlfred, codeBen);
constraint nonoverlap(codeAlfred, workMother);
constraint nonoverlap(codeBen, workMother);

solve minimize makeSpan;
% Exam scheduling example with just a set of 
% approved dates and *impossible* ones
include "globals.mzn";

% some soft constraint business
include "soft_constraints/soft_constraints.mzn";

int: n; set of int: STUDENT = 1..n; 
int: m; set of int: DATE = 1..m;
array[STUDENT] of set of DATE: possibles;
array[STUDENT] of set of DATE: impossibles;
array[STUDENT] of string: studentNames;
array[DATE] of string: dateNames;

% the actual decisions
array[STUDENT] of var DATE: scheduled;

int: minPerSlot; int: maxPerSlot;

% some default values for now
minPerSlot = 0; maxPerSlot = 4;

constraint global_cardinality_low_up ( scheduled, [d | d in DATE], [minPerSlot | d in DATE], [maxPerSlot | d in DATE]); 

% try and satisfy hard constraints at all costs
constraint forall(s in STUDENT) (not (scheduled[s] in impossibles[s])); 
 
% have a soft constraint for every student
nScs = n;
penalties = [ 1 | n in STUDENT]; % they are equally important in this case 

constraint forall(s in STUDENT) ( (scheduled[s] in possibles[s]) <-> satisfied[s] ) ;
var DATE: scheduledDates;
constraint  nvalue(scheduledDates, scheduled);
var 0..nScs: violateds;
constraint violateds = penSum;

% redundant constraint 
int: lowerBound = (n div maxPerSlot) + ( n mod maxPerSlot > 0) * 1  ;
constraint scheduledDates >= lowerBound;

constraint penSum = 0;

solve 
:: int_search(satisfied, input_order, indomain_max, complete)
%:: int_search(scheduled, dom_w_deg, indomain_max, complete)
%search minimize_lex([scheduledDates, violateds]);  % pro teachers
%search minimize_lex([violateds, scheduledDates]); % pro students

minimize scheduledDates;

output [ "lowerBound: \(lowerBound) Penalties: \(violateds)\n ++ Scheduled: \(scheduled)\n ++ Distinct dates: \(scheduledDates)" ];
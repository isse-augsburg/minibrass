% Exam scheduling example 
include "globals.mzn";

% some soft constraint business
include "../soft_constraints.mzn";

int: n; 
set of int: STUDENT = 1..n; 

int: m; 
set of int: DATE = 1..m;

array[STUDENT] of set of DATE: possibles;
array[STUDENT] of set of DATE: impossibles;
array[STUDENT] of string: studentNames;
array[DATE] of string: dateNames;

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

solve 
:: int_search(satisfied, input_order, indomain_max, complete)
minimize penSum;

output [ "Penalties: \(penSum)\n ++ Scheduled: \(scheduled)" ];
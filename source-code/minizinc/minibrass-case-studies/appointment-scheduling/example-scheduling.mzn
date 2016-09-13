% Exam scheduling example 
include "globals.mzn";
include "example-scheduling_o.mzn";
include "fmsoft2016.mzn";
include "soft_constraints/pvs_gen_search.mzn";
int: n; 
set of int: STUDENT = 1..n; 

int: m; 
set of int: DATE = 1..m;

%array[STUDENT] of set of DATE: possibles;
%array[STUDENT] of set of DATE: impossibles;
%array[STUDENT] of string: studentNames;
%array[DATE] of string: dateNames;

array[STUDENT] of var DATE: scheduled;

int: minPerSlot; int: maxPerSlot;

% some default values for now
minPerSlot = 0; maxPerSlot = 4;

constraint global_cardinality_low_up ( scheduled, [d | d in DATE], [minPerSlot | d in DATE], [maxPerSlot | d in DATE]); 

% try and satisfy hard constraints at all costs
%constraint forall(s in STUDENT) (not (scheduled[s] in impossibles[s])); 
 

var DATE: scheduledDates;
constraint  nvalue(scheduledDates, scheduled);

% redundant constraint 
int: lowerBound = (n div maxPerSlot) + ( n mod maxPerSlot > 0) * 1  ;
constraint scheduledDates >= lowerBound;

solve 
search pvs_BAB();

output [ "lowerBound: \(lowerBound) ++ Scheduled: \(scheduled)\n ++ Distinct dates: \(scheduledDates)" ] ++ [ "\nValuations: mbr_overall_students = \(mbr_overall_students); mbr_overall_teachers = \(mbr_overall_teachers)\n"];
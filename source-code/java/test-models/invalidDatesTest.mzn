include "global_cardinality_low_up.mzn";
include "nvalue.mzn";
include "invalidDatesTest_o.mzn";
include "soft_constraints/minibrass.mzn"; 

% ==============================================
%
% Exam-Scheduling based on Approval Voting
% ----------------------------------------
% We schedule exam dates for s students with 
% restrictions on how many students can take
% the exam at a single date d
%
% ==============================================

int: d; % maximum number of dates
set of int: DATE = 1..d; 

int: minPerDate;
int: maxPerDate; 

int: s; % number of students
set of int: STUDENT = 1..s;


array[STUDENT] of string: studentNames;
array[DATE] of string: dateNames;

% the actual decisions
array[STUDENT] of var DATE: scheduled;

constraint global_cardinality_low_up ( scheduled, [d | d in DATE], [minPerDate | d in DATE], [maxPerDate | d in DATE]); 

var DATE: scheduledDates;
constraint  nvalue(scheduledDates, scheduled);

% redundant constraint 
int: lowerBound = (s div maxPerDate) + ( s mod maxPerDate > 0) * 1  ;
constraint scheduledDates >= lowerBound;


d = 10; % three available dates 

%studentNames = ["Meier", "Müller", "Reuß"];

dateNames = ["MonMorning", "MonEvening", "TueMorning", "TueEvening", "WedMorning", "WedEvening","ThuMorning", "ThuEvening","FriMorning", "FriEvening"];

minPerDate = 0;
maxPerDate = 2;


int: monMorning = 1;
int: monEvening = 2;
int: tueMorning = 3;
int: tueEvening = 4;
int: wedMorning = 5;
int: wedEvening = 6;
int: thuMorning = 7;
int: thuEvening = 8;
int: friMorning = 9;
int: friEvening = 10;

% solve satisfy;
solve search miniBrass();

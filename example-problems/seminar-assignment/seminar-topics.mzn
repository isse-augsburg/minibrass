% A student-company matching
include "globals.mzn";

include "sose2019.mzn"; % instance data 
include "seminar-topics_o.mzn";
include "soft_constraints/minibrass.mzn"; 


int: n; % number of students 
set of int: STUDENT = 1..n;
array[STUDENT] of string: studentNames;

int: t; % number of topics
set of int: TOPIC = 1..t;

int: l; % number of lecturers
set of int: LECTURER = 1..l;
array[LECTURER] of string: lecturerNames;
array[TOPIC] of LECTURER: topicToLecturer;


% assign students to topics
array[STUDENT] of var TOPIC: studentToTopic;
constraint alldifferent(studentToTopic); 

array[STUDENT] of var TOPIC: studentToLecturer;

constraint forall(s in STUDENT) (studentToLecturer[s] = topicToLecturer[studentToTopic[s]] );

% a global cardinality constraint % minimum 1 per company, maximal 3?

int: minPerLecturer; int: maxPerLecturer;

% some default values for now
minPerLecturer = 0; maxPerLecturer = 3;

constraint global_cardinality_low_up ( studentToLecturer, [l_ | l_ in LECTURER], [minPerLecturer | l_ in LECTURER], [maxPerLecturer | l_ in LECTURER]); 

solve 
:: pvsSearchHeuristic %
search miniBrass();

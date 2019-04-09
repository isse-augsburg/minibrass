% A student-company matching
include "globals.mzn";

int: n; % number of students 
set of int: STUDENT = 0..n-1;

int: m; % number of companies
set of int: COMPANY = 0..m-1;

% assign students to companies, not the other way around
array[STUDENT] of var COMPANY: worksAt;

% a global cardinality constraint % minimum 1 per company, maximal 3?
int: minPerCompany; int: maxPerCompany;

% some default values for now
% minPerCompany = 1; maxPerCompany = 2;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 
%constraint worksAt = array1d(0..17, [0, 1, 2, 1, 5, 3, 5, 2, 4, 1, 2, 0, 5, 0, 4, 3, 3, 4]);

solve 
%:: int_search(worksAt, input_order, indomain_random, complete)
satisfy;

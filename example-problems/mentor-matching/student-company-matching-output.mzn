% A student-company matching
include "globals.mzn";

include "wise2017.mzn"; % instance data 
%include "student-company-matching_o.mzn";
%include "soft_constraints/minibrass.mzn"; 


int: n; % number of students 
set of int: STUDENT = 1..n;
array[STUDENT] of string: studentNames;

int: m; % number of companies
set of int: COMPANY = 1..m;
array[COMPANY] of string: companyNames;

% assign students to companies, not the other way around
array[STUDENT] of var COMPANY: worksAt;

% a global cardinality constraint % minimum 1 per company, maximal 3?
int: minPerCompany; int: maxPerCompany;

% some default values for now
minPerCompany = 1; maxPerCompany = 3;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 

constraint worksAt = [8, 5, 1, 1, 8, 10, 5, 2, 2, 9, 3, 6, 1, 4, 8, 7, 3];

solve 
satisfy;

output [ "\(studentNames[s]) works at \(companyNames[worksAt[s]])\n" | s in STUDENT]; 
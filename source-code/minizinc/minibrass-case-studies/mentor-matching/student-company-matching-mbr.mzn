% A student-company matching
include "globals.mzn";

include "fmse2016.mzn"; % instance data 
include "student-company-matching_o.mzn";
include "soft_constraints/pvs_gen_search.mzn"; 


int: n; % number of students 
set of int: STUDENT = 1..n;

int: m; % number of companies
set of int: COMPANY = 1..m;

% assign students to companies, not the other way around
array[STUDENT] of var COMPANY: worksAt;

% a global cardinality constraint % minimum 1 per company, maximal 3?
int: minPerCompany; int: maxPerCompany;

% some default values for now
minPerCompany = 1; maxPerCompany = 3;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 

solve 
search pvs_BAB();

output [ "worksAt = \(worksAt)"] ++ 
[ "\nValuations: mbr_overall_ToWeighted_RefTo_companies_ = \(mbr_overall_ToWeighted_RefTo_companies_); mbr_overall_ToWeighted_RefTo_students_ = \(mbr_overall_ToWeighted_RefTo_students_)\n"];

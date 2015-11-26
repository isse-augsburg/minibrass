% A student-company matching
include "globals.mzn";

% some soft constraint business
include "../soft_constraints.mzn";

include "../spd_worse.mzn"; % the actual isBetter predicate
include "../tpd_worse.mzn"; % the actual isBetter predicate

include "../cr-consistency.mzn";

% binding to the concrete PVS in use has to be
% done using pvs-xyz.mzn
include "../pvs-spd.mzn";

int: n; % number of students 
set of int: STUDENT = 1..n;

int: m; % number of companies
set of int: COMPANY = 1..m;

% assign students to companies, not the other way around
array[STUDENT] of var COMPANY: worksAt;

% inverse on sets?
% array[COMPANY] of var opt set of STUDENT: hasStudent = [ { s | s in STUDENT where worksAt[s] = c} | c in COMPANY ] ;
% MiniZinc: internal error: var set comprehensions not supported yet

% a global cardinality constraint % minimum 1 per company, maximal 3?
int: minPerCompany; int: maxPerCompany;

% some default values for now
minPerCompany = 1; maxPerCompany = 4;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 

% hides the data (is however defined as .mzn since we want names)
include "ws15.mzn";

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");

solve 
%:: int_search(satisfied, input_order, indomain_max, complete)
:: int_search([ satisfied[mostImpFirst[i]] | i in SOFTCONSTRAINTS], input_order, indomain_max, complete)
minimize penSum;

output [ "worksAt = \(worksAt), penalty: \(penSum)"];
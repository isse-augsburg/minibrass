% A student-company matching
include "globals.mzn";

% some soft constraint business
include "soft_constraints/soft_constraints.mzn";

include "soft_constraints/spd_worse.mzn"; % the actual isBetter predicate
include "soft_constraints/tpd_worse.mzn"; % the actual isBetter predicate

include "soft_constraints/cr_consistency.mzn";

include "minisearch.mzn";

% binding to the concrete PVS in use has to be
% done using pvs-xyz.mzn
include "soft_constraints/pvs_spd.mzn";

int: n; % number of students 
set of int: STUDENT = 1..n;

int: m; % number of companies
set of int: COMPANY = 1..m;

% assign students to companies, not the other way around
array[STUDENT] of var COMPANY: worksAt;

% a global cardinality constraint % minimum 1 per company, maximal 3?
int: minPerCompany; int: maxPerCompany;

% some default values for now
minPerCompany = 1; maxPerCompany = 2;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 

% this has to be tailored to the problem instance
int: lastStudentPref;

% now some rather easy things: split the objectives

% this is intended for a lexicographic search 
% - we still need the objective for difference between most students and least students
var int: penStud;
constraint penStud = sum(sc in 1..lastStudentPref) (bool2int(not satisfied[sc]) * penalties[sc]);
var int: penComp; 
constraint penComp = sum(sc in lastStudentPref+1..nScs) (bool2int(not satisfied[sc]) * penalties[sc]);

% hides the data (is however defined as .mzn since we want names, e.g., int: frank = 1)
include "toy-mentoring.mzn";

constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");

solve 
:: int_search([ satisfied[mostImpFirst[i]] | i in SOFTCONSTRAINTS], input_order, indomain_max, complete)
%search minimize_lex([penStud, penComp]) /\ if hasSol() then print() else print("No solution found\n") endif;
search minimize_lex([penComp, penStud]) /\ if hasSol() then print() else print("No solution found\n") endif;

output [ "worksAt = \(worksAt), penalty Students: \(penStud), penalty Companies: \(penComp)"];
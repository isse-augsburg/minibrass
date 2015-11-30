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
% make that variable, with maxPerCompany = minPerCompany + diff
% and minimize diff with highest priority

% some default values for now
minPerCompany = 2; maxPerCompany = 3;

constraint global_cardinality_low_up ( worksAt, [c | c in COMPANY], [minPerCompany | c in COMPANY], [maxPerCompany | c in COMPANY]); 

% this has to be tailored to the problem instance
int: lastStudentPref;

% now some rather easy things: split the objectives

% this is intended for a lexicographic search 
% - we still need the objective for difference between most students and least students
var int: penStud = sum(sc in 1..lastStudentPref) (bool2int(not satisfied[sc]) * penalties[sc]);
var int: penComp = sum(sc in lastStudentPref+1..nScs) (bool2int(not satisfied[sc]) * penalties[sc]);

% or build up a new array of penalties, weighted by some coefficient
int: studentFactor = 5;
int: companyFactor = 1;

array[SOFTCONSTRAINTS] of int: corrPenalties = [ if s <= lastStudentPref then 
                                                    studentFactor * penalties[s] 
                                                 else
                                                    companyFactor * penalties[s]
                                                 endif
                                        | s in SOFTCONSTRAINTS ] ;

var int: penCorrected = sum(sc in SOFTCONSTRAINTS) (bool2int(not satisfied[sc]) * corrPenalties[sc]);

% find the sorted permutation of soft constraint instances
array[SOFTCONSTRAINTS] of SOFTCONSTRAINTS: sortPermCorrScs = arg_sort(corrPenalties);
% invert, since arg_sort use <= and we need decreasing order
array[SOFTCONSTRAINTS] of SOFTCONSTRAINTS: mostImpFirstCorr = [ sortPermCorrScs[nScs-s+1] | s in SOFTCONSTRAINTS]; 


% hides the data (is however defined as .mzn since we want names)
include "ws15.mzn";

%constraint assert(consistentCR(SOFTCONSTRAINTS, crEdges), "Constraint relationship is not consistent");

solve 
%:: int_search(satisfied, input_order, indomain_max, complete)
%:: int_search([ satisfied[mostImpFirst[i]] | i in SOFTCONSTRAINTS], input_order, indomain_max, complete)
:: int_search([ satisfied[mostImpFirstCorr[i]] | i in SOFTCONSTRAINTS], input_order, indomain_max, complete)
%minimize penSum;
minimize penCorrected;
%satisfy;
% worksAt = [1, 1, 4, 3, 3, 2, 2, 3, 5, 7, 6, 6, 6, 5, 2, 5], penalty: 1003 proved optimal in 2m 12s
% worksAt = [1, 1, 1, 4, 3, 2, 2, 3, 5, 7, 6, 6, 6, 5, 3, 5], penalty: 1002 Finished in 4m 31s
% worksAt = [1, 7, 1, 4, 3, 2, 2, 3, 5, 7, 6, 6, 4, 5, 3, 5], penalty: 1014
output [ "worksAt = \(worksAt), penalty: \(penCorrected)"];
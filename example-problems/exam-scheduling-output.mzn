% Exam scheduling example 
include "globals.mzn";

int: n; 
set of int: STUDENT = 1..n; 

int: m; 
set of int: DATE = 1..m;

array[STUDENT] of set of DATE: possibles;
array[STUDENT] of set of DATE: impossibles;
array[STUDENT] of string: studentNames;
array[DATE] of string: dateNames;

array[STUDENT] of DATE: scheduled = [5, 5, 2, 2, 5, 2, 1, 4, 3, 4, 1, 3, 1, 3, 4];
solve satisfy;

output [ if scheduled[s] = d then "\(dateNames[d]) : \(studentNames[s])\n" else "" endif  | d in DATE, s in STUDENT];
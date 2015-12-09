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

array[STUDENT] of DATE: scheduled = [3, 3, 8, 9, 8, 8, 9, 8, 7, 7, 7, 7, 9, 3, 1, 3, 2];
solve satisfy;

output [ if scheduled[s] = d then "\(dateNames[d]) : \(studentNames[s])\n" else "" endif  | d in DATE, s in STUDENT];
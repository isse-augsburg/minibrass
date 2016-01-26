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

array[STUDENT] of DATE: scheduled = [7, 6, 10, 12, 12, 10, 11, 10, 9, 9, 9, 11, 11, 7, 5, 6, 6, 7, 7, 5, 11, 10, 9, 8, 5, 12, 8, 12, 6, 8, 5, 8, 4];
solve satisfy;

output [ if scheduled[s] = d then "\(dateNames[d]) : \(studentNames[s])\n" else "" endif  | d in DATE, s in STUDENT];
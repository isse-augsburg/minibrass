% just a test for the correct variable ordering
 
% walk through with gist or print all solutions
% to see that indeed the most important variables 
% are tried first

% number of soft constraints 
include "../soft_constraints.mzn";

penalties = [3, 1, 5];

nScs = 3;


solve % I want to try and maximize all of them first
:: int_search([ satisfied[mostImpFirst[i]] | i in SOFTCONSTRAINTS], input_order, indomain_max, complete)
satisfy;

output["satisfied = \(satisfied) penSum = \(penSum) mostImpFirst = \(mostImpFirst)"]; 
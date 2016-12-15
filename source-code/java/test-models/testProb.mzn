var 1..5: s1;
var 5..10: s2; 

include "testProb_o.mzn";
% include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

solve
% search pvs_BAB();
maximize topLevelObjective; 

output ["s1 = \(s1); s2 = \(s2)"] ++ [ "\nValuations: topLevelObjective = \(topLevelObjective)\n"];


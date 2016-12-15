var 0..5: s1;
var 5..10: s2; 

include "testProb_o.mzn";
% include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

constraint 2*s1 + 3 * s2 <= 32;

solve
% search pvs_BAB();
maximize topLevelObjective; 

output ["s1 = \(s1); s2 = \(s2)"] ++ [ "\nValuations: topLevelObjective = \(topLevelObjective)\n"];


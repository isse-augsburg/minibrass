include "pvsRelation_o.mzn";
include "soft_constraints/minibrass.mzn"; 


var 1..3: a; 

solve search miniBrass();

output ["a = \(a);"] ++ [ "\nValuations:  topLevelObjective = \(topLevelObjective)\nmbr_tuples_agent = \(mbr_tuples_agent)"];

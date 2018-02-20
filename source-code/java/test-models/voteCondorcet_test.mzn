include "voteCondorcet_o.mzn";
include "soft_constraints/minibrass.mzn"; 


var 1..4: a; 

solve 
:: int_search([a], input_order, indomain_min,complete)
search miniBrass();

output ["a = \(a);"] ++ [ "\nValuations: mbr_overall_agent1 = \(mbr_overall_agent1); mbr_overall_agent2 = \(mbr_overall_agent2)\n"];

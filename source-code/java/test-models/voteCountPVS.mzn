include "voteCountPVS_o.mzn";
include "soft_constraints/minibrass.mzn"; 


var 1..3: a; 

int: s; % to be bound by MiniBrass
array[1..s] of string: names;
 
solve 
:: int_search([a], input_order, indomain_min,complete)
search miniBrass();

output ["a = \(a);"] ++ [ "\nValuations: mbr_overall_agent1 = \(mbr_overall_agent1); mbr_overall_agent2 = \(mbr_overall_agent2); mbr_overall_agent3 = \(mbr_overall_agent3)\n"];

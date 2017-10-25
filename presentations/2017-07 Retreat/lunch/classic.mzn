
include "classic_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

set of int: BEER = 1..10;

var BEER: monbeer; 
var BEER: tuesbeer; 

constraint tuesbeer = 3 /\ monbeer = 6;

constraint tuesbeer < monbeer;

solve 
minimize topLevelObjective;

output ["monbeer = \(monbeer); tuesbeer = \(tuesbeer); "] ++ [ "\nValuations:  topLevelObjective = \(topLevelObjective)\n"]
++  [ "\nValuations: mbr_overall_beers = \(mbr_overall_beers)\n"];

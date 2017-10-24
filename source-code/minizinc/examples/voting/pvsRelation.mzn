include "pvsRelation_o.mzn";
include "soft_constraints/minibrass.mzn"; 

function ann: miniBrass = pvs_BAB;
var 1..3: a; 

solve search miniBrass();

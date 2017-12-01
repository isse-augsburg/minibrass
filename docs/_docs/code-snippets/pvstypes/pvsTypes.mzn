include "soft_constraints/minibrass.mzn";
include "pvsTypes_o.mzn";

var 0..3: x;
var 0..3: y;

solve search miniBrass();

include "cost-functions_o.mzn";
include "soft_constraints/minibrass.mzn";

var 0..1: x; var 0..1: y; var 0..1: z;

constraint x == 1 /\ y == 1 /\ z == 0;

solve search miniBrass();

var 0..2: x; var 0..2: y; var 0..2: z;

var bool: sc1 = (x + 1 = y);
var bool: sc2 = (z = y + 2);

solve minimize 2 * (not sc1) + (not sc2); 
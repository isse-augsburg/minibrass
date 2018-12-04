
% the basic, simple "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
%search pvs_BAB();
satisfy;

output ["x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations:  mbr_overall_cr1 = \(mbr_overall_cr1)\n"];





include "classicNoSearchPareto_o.mzn"; % output of minibrass
%include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

% the basic, simple "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
%search pvs_BAB();
satisfy;

output ["x = \(x); y = \(y); z = \(z)"] ++ [ "\nValuations:  mbr_overall_cr1 = \(mbr_overall_cr1)\n"];




constraint ( (is_worse_constraint_pref((1..3), mbr_overall_cr1, mbr_nScs_cr1, mbr_crEdges_cr1, mbr_useSPD_cr1)) /\ (is_worse_constraint_pref((1..3), mbr_overall_cr2, mbr_nScs_cr2, mbr_crEdges_cr2, mbr_useSPD_cr2)) );
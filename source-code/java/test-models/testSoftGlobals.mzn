include "testSoftGlobals_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate
include "soft_constraints/soft_alldifferent.mzn";

int: n = 5; int: m = 4;
set of int: STUDENT = 1..n;
set of int: PROJECT = 1..m;

array[STUDENT] of var PROJECT: x;
% var int: maxPerProj; constraint soft_alldifferent(x, maxPerProj, true);
%

output["x = \(x)"] ++ [ "\nValuations: mbr_overall_cfn1 = \(mbr_overall_cfn1)\n"];
solve search pvs_BAB();

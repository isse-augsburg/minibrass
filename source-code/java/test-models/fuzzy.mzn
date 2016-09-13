% A fuzzy CSP example from the paper: "Preferences in Constraint Satisfaction and Optimization", Francesca Rossi, K. Brent Venable, and Toby Walsh, AI Magazine, 2008
% this model uses float_array_element, which is currently not supported by Gecode
% therefore, for testing purposes please use: 
%
%  minisearch -Gjacop --solver fzn-jacop fuzzy.mzn
%

include "fuzzy_o.mzn"; % output of minibrass
include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate
include "soft_constraints/fuzzy_encoding.mzn";

% the basic, "classic" CSP 
set of int: DOM = 1..3;

int: fish = 0; int: meat = 1;
var {fish, meat}: mainCourse;

int: white = 0; int: red = 1;
var {white, red}: wine;

int: noon = 0; int: one = 1;
var {noon, one}: lunch;

int: two = 0; int: three = 1;
var {two, three}: swim;

solve 
search pvs_BAB();

output ["mainCourse = \(mainCourse); wine = \(wine); lunch = \(lunch); swim = \(swim)"] ++ [ "\nValuations:  mbr_overall_fz1 = \(mbr_overall_fz1)\n"];

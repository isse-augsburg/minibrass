%include "smallExample_o.mzn";
include "huette2017_o.mzn";
include "soft_constraints/minibrass.mzn"; 
include "link_set_to_booleans.mzn";
% ==============================================
%
% Lunch-Deselection based on Voting
% ----------------------------------------
% We select which lunch *NOT* to cook 
% anymore based on several agents' preferences
%
% ==============================================

int: f; 
set of int: FOOD = 1..f;

int: gulasch = 1; int: braten = 2; int: bolognese = 3; int: grillen = 4;
int: wurstsalat = 5; int: burger = 6;

array[FOOD] of string: foodNames;

% the actual decisions
var set of FOOD: selected; 
array[FOOD] of var bool: selectedBool;

var FOOD: deselected;

constraint not(deselected in selected);
% predicate link_set_to_booleans(var set of int: s, array[int] of var bool: b)
constraint link_set_to_booleans(selected, selectedBool);
constraint sum(s in FOOD)(bool2int(selectedBool[s])) == f-1;

solve 
%:: int_search([deselected], input_order, indomain_max, complete) 
%satisfy;
search miniBrass();

include "genOutput.mzn";


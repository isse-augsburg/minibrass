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
include "globals.mzn";

% the actual decisions
var set of FOOD: selected; 
var set of FOOD: deselected;

array[FOOD] of var bool: selectedBool;

% parameters for counting
par int: selectedCardinality;


% predicate link_set_to_booleans(var set of int: s, array[int] of var bool: b)
constraint link_set_to_booleans(selected, selectedBool);
constraint deselected = FOOD diff selected;
%function var int: count(array [int] of var int: x, var int: y)
constraint count(bool2int(selectedBool), 1) = selectedCardinality;
 
solve satisfy;

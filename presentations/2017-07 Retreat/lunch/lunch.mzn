include "soft_constraints/pvs_gen_search.mzn"; % for minibrass-search style using the generated predicate

int: numberOfPeople = 5;
set of int: PEOPLE = 1..numberOfPeople;

include "link_set_to_booleans.mzn";
include "lunch_o.mzn"; % minibrass compiled output

int: foods = 6;
int: reducedFoods = 5;

set of int: FOOD = 1..foods;
int: burger = 1;
int: schweinebraten = 2; 
int: grillen = 3; 
int: bolognese = 4;
int: gulasch = 5;
int: wurstsalat = 6;

array[FOOD] of var bool: dinnerSelected;
var set of FOOD: dinners;



constraint link_set_to_booleans(dinners,dinnerSelected);
constraint sum(dinnerSelected) = reducedFoods;
solve search pvs_BAB();

output [ "\nValuations: mbr_overall_foodPvs = \(mbr_overall_foodPvs)\n"];
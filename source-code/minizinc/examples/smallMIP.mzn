include "soft_constraints/soft_constraints_noset.mzn";
include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";
% ground penalties using the appropriate weighting
penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, true) | s in SOFTCONSTRAINTS];

int: T = 5; set of int: WINDOW = 1..T;
array[WINDOW] of float: demand = [10.0, 11.3, 15.2, 20.7, 19.2];

int: P = 3; set of int: PLANTS = 1..P;

array[PLANTS] of float: pMin  = [12.0, 5.0, 7.3];
array[PLANTS] of float: pMax  = [15.0, 11.3, 9.7];
array[PLANTS] of float: price = [3.2, 4.8, 2.9];

array[WINDOW, PLANTS] of var 0.0..15.0: supply; 
var float: obj;
constraint obj = sum(w in WINDOW) ( abs( sum(p in PLANTS) (supply[w, p]) - demand[w] ) );

% a soft constraint - plant 2 wants to provide at least 6 at time 1
% I want two soft constraints
nScs = 2;

% this is calculated 
%penalties = [2,1];

constraint supply[1, 2] >= 6.0 <-> satisfied[1]; 
constraint supply[2, 2] >= 6.0 <-> satisfied[2]; 

% constraint time step 1 seems more urgent
nCrEdges = 1;
crEdges = [| 2, 1 |];

solve minimize obj + penSum;

output[ "Obj = \(obj)\nsupply=\(supply)\npenSum=\(penSum)\npenalties=\(penalties)"];
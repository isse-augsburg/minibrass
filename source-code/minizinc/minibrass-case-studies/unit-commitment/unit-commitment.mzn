int: T = 5; set of int: WINDOW = 1..T;
array[WINDOW] of int: demand = [20, 21, 25, 30, 29];

int: P = 3; set of int: PLANTS = 1..P;
int: biogas = 1; int: EV = 2; int: thermal = 3;

array[PLANTS] of int: pMin  = [12, 5, 7];
array[PLANTS] of int: pMax  = [15, 11, 9];
array[PLANTS] of int: price = [3, 5, 3];

array[WINDOW, PLANTS] of var 0..15: supply; 

constraint forall(p in PLANTS, w in WINDOW) (supply[w,p] in pMin[p]..pMax[p]);

array[WINDOW] of var int: violation = [ abs( sum(p in PLANTS) (supply[w, p]) - demand[w] ) | w in WINDOW];
var int: sviolation = sum(violation);

%solve minimize sviolation ;
solve satisfy;

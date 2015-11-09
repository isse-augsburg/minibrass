% my alltime favorite small example 
include "link_set_to_booleans.mzn";
%include "minisearch.mzn"; % include the search minisearch lite
include "spd_better.mzn";
var 1..3: x; 
var 1..3: y;
var 1..3: z;

int: pc1 = 3;
int: pc2 = 1;
int: pc3 = 1;

var {0, pc1}: penC1;
var {0, pc2}: penC2;
var {0, pc3}: penC3;

int: nScs = 3;
set of int: SOFTCONSTRAINTS = 1..nScs;

array[SOFTCONSTRAINTS] of var bool: satisfied;
array[SOFTCONSTRAINTS] of var bool: violated = [not satisfied[sc] | sc in SOFTCONSTRAINTS] ;

array[SOFTCONSTRAINTS] of int: penalties = [pc1,pc2,pc3];

var int: penSum = sum(sc in SOFTCONSTRAINTS) (bool2int(not satisfied[sc]) * penalties[sc]);
var set of SOFTCONSTRAINTS: violatedScs;

constraint link_set_to_booleans(violatedScs,violated); 

constraint x + 1 = y <-> satisfied[1];
constraint z = y + 2 <-> satisfied[2];
constraint x + y <= 3 <-> satisfied[3];

% now just some edge business 
int: c1 = 1;
int: c2 = 2; % c1 <- c2, c1 is more important than c2
int: c3 = 3; % c1 <- c3, c1 is also more important than c3
int: nEdges = 2;
array[1..nEdges, 1..2] of SOFTCONSTRAINTS: edges = [| c2, c1 
                                                    | c3, c1 |];

constraint spd_better({c2,c3}, violatedScs,SOFTCONSTRAINTS,edges);
%constraint penC1 = 0 /\ penC2 = 0;
%constraint penSum = 0;
solve 
:: int_search(satisfied, input_order, indomain_min, complete)
%:: int_search([penC1,penC2,penC3, x, y, z], input_order, indomain_min, complete)
minimize penSum;

output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x), y  -> \(y), z -> \(z)"];

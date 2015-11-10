% my alltime favorite small example 
include "link_set_to_booleans.mzn";
include "minisearch.mzn"; % include the search minisearch lite
%include "spd_better.mzn";

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

include "alldifferent_except_0.mzn";

predicate spd_better(var set of SOFTCONSTRAINTS: lhs, var set of SOFTCONSTRAINTS: rhs, 
                     set of int: softConstraints,
                     array[int, 1..2] of int: edges
) = 
let {
  int: le = min(index_set_1of2(edges));
  int: ue = max(index_set_1of2(edges));

  var set of int: lSymDiff = lhs diff rhs;
  var set of int: rSymDiff = rhs diff lhs;
  set of int: softConstraints0 = {0} union SOFTCONSTRAINTS;
  % 0 represents noVal for constraints not in the right-hand-side

  var set of int: rUndefined = softConstraints diff rSymDiff;

  % I need to make the dominance explicit by a function
  array[SOFTCONSTRAINTS] of var softConstraints0: witness; 

  % collect all predecessors such that succ in lessThans[pred] iff succ less than pred
  array[SOFTCONSTRAINTS] of set of SOFTCONSTRAINTS: lessThans = 
    [ {succ | succ in SOFTCONSTRAINTS where exists(e in le..ue) (edges[e,1] = pred /\ edges[e,2] = succ)} | pred in SOFTCONSTRAINTS];

} in (
  lhs != rhs /\
  alldifferent_except_0(witness) /\  forall(s in softConstraints) (s in rUndefined -> witness[s] = 0) /\ 
  forall(s in softConstraints) (s in rSymDiff -> (witness[s] in lSymDiff /\ witness[s] in lessThans[s]))     
);


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

%constraint violatedScs != {c1,c2,c3}; % spd_better({c1, c2,c3}, violatedScs,SOFTCONSTRAINTS,edges);
constraint spd_better({c1, c2,c3}, violatedScs,SOFTCONSTRAINTS,edges);
%constraint penC1 = 0 /\ penC2 = 0;
%constraint penSum = 0;

function ann: mybab(var set of SOFTCONSTRAINTS: violatedScs) =
       repeat(
         %  scope (
           if next() then
               let {
                 set of SOFTCONSTRAINTS: ub = sol(violatedScs); 
               } in (
                 print("Violated scs now: \(ub)\n") /\
                 print("Intermediate solution with objective \(sol(penSum)) and assignment x -> \(sol(x)), y -> \(sol(y)), z -> \(sol(z))\n") /\
                 commit() /\ post(spd_better(ub, violatedScs, SOFTCONSTRAINTS, edges))
               )
           else break endif
        %   )
       );


solve 
:: int_search(satisfied, input_order, indomain_min, complete)
%:: int_search([penC1,penC2,penC3, x, y, z], input_order, indomain_min, complete)
%minimize penSum;
search time_limit(3000, mybab(violatedScs)) /\ if hasSol() then print() else print("No solution found\n") endif;


output ["Obj: \(penSum) by violating {\(violatedScs) } : x -> \(x), y  -> \(y), z -> \(z)"];

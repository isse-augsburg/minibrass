% this would be the type definition file for the inclusion constraint valuation pvs
include "link_set_to_booleans.mzn";

function var set of int: link_booleans(array[int] of var bool: b) =
let {
  
  var set of index_set(b): resultSet;
  constraint link_set_to_booleans(resultSet,b); 
} in resultSet;

% {1,2} is worse than {1}
predicate is_worse(var set of int: set1, var set of int: set2) = set2 subset set1;

var set of 1..2: decide;
constraint is_worse(decide, {1}) /\ decide != {1};
constraint is_worse(decide, {}); 

array[int] of string: out = ["This works with decide: \(decide)", show(decide) ];
output out;
solve satisfy;

/* 
We need for a PVS type definition:

PVSType<E, S>

Element type (E)                   ... set of SOFTCONSTRAINTS 
Specification type (S)             ... bool
Aggregation function : (S^n -> E)  ... link_booleans(array[SOFTCONSTRAINTS] of var bool: b) = set of SOFTCONSTRAINTS
Ordering relation R sub E x E      ... is_worse
Best element                       ... {}

Make sure there is a unique embedding mu : S -> E and a multiplication times : E^n -> E 
such that mu(s_1) times ... times mu(s_n) = aggregate(s_1, ..., s_n) 
*/

/* 
What we generate for a PVSType<E, S>(n, times o mu : S^n -> E, leq : E x E, top : E)

array[1..n] of var S: softConstraintResults; % the applications of the individual soft constraints into the specification type
var E: overall; 
constraint overall = times(softConstraintResults); 
constraint forall(i in 1..n) (is_worse(softConstraintResults[i], top) ); % maybe not even necessary, useful
for minisearch: propagate "post( is_worse(sol(overall), overall) )" to BaB 
for combined PVS (e.g. PVS_1 x PVS_2): post (is_worse(sol(overall_1), sol(overall_2), overall1, overall2 )
generate string list of outputs of all overall values so minisearch can access them 
*/

/* What would be a PVS-morphism? 
Let T_1 = PVSType<E_1, S_1>(n_1, times_1, leq_1, top_1) and 
    T_2 = PVSType<E_2, S_2>(n_2, times_2, leq_2, top_2)

We then have a morphism f : T_1 -> T_2 if (letting e_i in E_1 ... f(e_i) in E_2):
n_1 = n_2 = n, f(top_1) = top_2, f(e_1 times_1 ... times_1 e_n) = f(e_1) times_2 ... times_2 f(e_n)
e_1 leq_1 e_2 -> f(e_1) leq_2 f(e_2)

What do we need to provide:

- function var S_2: map(var S_1: s_1, ..., var S_n: s_n, par T_1: t_1, ..., par T_m: t_m)
- for vector-valued output, we'd need to have map_1 through map_k (for starters, use only one type) 


*/
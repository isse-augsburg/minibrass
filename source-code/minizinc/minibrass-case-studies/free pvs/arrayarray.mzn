int: maxP = 3;
set of int: P = 1..maxP;
int: k; set of int: OCC = 0..k;
k = 2;
array[1..numberOfSoftConstraints, P] of var OCC: Ts;

int: numberOfSoftConstraints = 3;
set of int: MOCC = 0..k*numberOfSoftConstraints;

array[P] of var MOCC: T;

var 0..1: x;
var 0..1: y;

array[P] of var OCC: sc1 = embed(x == 0,1);

function array[P] of var OCC: embed(var bool: expression, par int: p) = let 
 {
   array[P] of var OCC: ret;
   constraint expression -> ret == [0,0,0];
   constraint not(expression) -> (forall(q in P) ( (ret[q] = 0 /\ p != q) \/ (ret[q] = 1 /\ p = q) ) ); 
 } in ret;

array[P] of var OCC: sc2;
constraint y == 0 -> sc2 == [0,0,0];
constraint y == 1 -> sc2 == [0,1,0];

constraint x  = 1 /\ y = 1 ;

constraint forall(p in P) (Ts[1,p] = sc1[p] /\ Ts[2,p] = sc2[p]);

%function var set of int: link_invert_booleans(array[int] of var bool: b, 
%  par int: nScs, array[int, 1..2] of par int: crEdges, par bool: useSPD)

function array[int] of var MOCC: multiset_union(array[int,int] of var OCC: scGradings,
                                                par int: nScs,
                                                par int: maxOccPerSc) =
let {
  set of int: UP = index_set_1of2(scGradings);
  set of int: MOCC = 0..nScs*maxOccPerSc;
  array[UP] of var MOCC: unioned;
  constraint forall(p in UP) (unioned[p] = sum(i in 1..nScs)(scGradings[i,p]) );
}
in unioned;

constraint T = multiset_union(Ts, numberOfSoftConstraints, k);
solve satisfy;
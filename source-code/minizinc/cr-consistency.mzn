% cr-consistency.mzn ------------
% -------------------------------
% This library offers methods
% to validate constraint relationships
% including a check for cycles
% -------------------------------

% just checks naively from each soft constraint if there is a cycle
function bool: isCyclic(set of int: softConstraints, 
                   array[int] of set of int: dominees) = (
  exists(s in softConstraints) (findCycle(softConstraints, s, {}, dominees) = true) 
);

% recursive method called by isCyclic to perform DFS exploration for cycles
function bool: findCycle(set of int: softConstraints, int: sc, set of int: alreadySeen,
                   array[int] of set of int: dominees) = 
let {
  set of int: nextSeen = {sc} union alreadySeen;
} in
(
  if sc in alreadySeen then true else 
     exists(s in dominees[sc]) (findCycle(softConstraints, s, nextSeen, dominees)) 
  endif
);
 
% Utility function that validates a given constraint relationship
% - verifies correct definition of edges
% - verifies that edges represent a DAG
function bool: consistentCR(set of int: softConstraints,
                            array[int, int] of int: edges) = 
                  % parameter range check is *not* done as of now by MZ
assert(min(index_set_2of2(edges)) = 1 /\ max(index_set_2of2(edges)) = 2
       /\ forall(e in index_set_1of2(edges), d in index_set_2of2(edges)) (edges[e,d] in softConstraints),
       "Edges must be of type [int:, 1..2] and point to nodes in the set of soft constraints.",
let {
  int: le = min(index_set_1of2(edges));
  int: ue = max(index_set_1of2(edges));


  array[softConstraints] of set of softConstraints: dominees = 
     [ {succ | succ in softConstraints where exists(e in le..ue) 
         (edges[e,1] = succ /\ edges[e,2] = pred)} | pred in softConstraints];

  bool: isCycle = isCyclic(softConstraints, dominees);
} in
(assert(not isCycle, "Relationship is cyclic!")));

% Recursive utility for predecessor/successor search
% basically just dfs
function bool: isPred(int: pred, int: succ, 
                      set of int: softConstraints,
                      array[int] of set of int: dominees) =
assert(pred in softConstraints /\ succ in softConstraints
       /\ forall(s in index_set(dominees)) (dominees[s] subset softConstraints), 
       "Inconsistent types found!", 
 (pred in dominees[succ] \/ exists(d in dominees[succ]) (
  isPred(pred, d, softConstraints, dominees)
) )
);

% Utility function that calculates the transitive 
% closure of a given constraint relationship
% - verifies that pred is a predecessor of succ
function array[int, int] of bool: 
               transClosureInt(set of int: softConstraints,
                               array[int, int] of int: edges) = 
%assert(min(index_set_2of2(edges)) = 1 /\ max(index_set_2of2(edges)) = 2
%       /\ forall(e in index_set_1of2(edges), d in index_set_2of2(edges)) (edges[e,d] in softConstraints),
%       "Edges must be of type [int:, 1..2] and point to nodes in the set of soft constraints.",
let {
  array[softConstraints] of set of softConstraints: dominees = 
     [ {succ | succ in softConstraints where exists(e in index_set_1of2(edges)) 
         (edges[e,1] = succ /\ edges[e,2] = pred)} | pred in softConstraints];
} in
(getAdjacencyMatrix(softConstraints, dominees))
;

function array[int, int] of bool: getAdjacencyMatrix(set of int: sc, 
array[int] of set of int: dominees) = let 
{
  array[sc, sc] of bool: adjacencyMatrix = 
        array2d(sc, sc, [ %true 
  isPred(pred,succ,sc,dominees)
| pred in sc, succ in sc]);
} in (adjacencyMatrix);

% Utility function that calculates the transitive 
% closure of a given constraint relationship
% - verifies that pred is a predecessor of succ
% - does not check for cycles, this has to be
%   done by consistentCR to avoid multiple 
%   calculations
function array[int, int] of int: 
               transClosure(set of int: softConstraints,
                            array[int, int] of int: edges) =
                  % parameter range check is *not* done as of now by MZ
let {
  array[softConstraints,softConstraints] of bool: tc = transClosureInt(softConstraints, edges); 
  int: nEdges = sum(s in softConstraints, p in softConstraints)(bool2int(tc[s,p]) );
} 
in (
  convertBoolToEdges(tc, nEdges, softConstraints, edges)
);

function array[int, int] of int: 
               convertBoolToEdges(array[int, int] of bool: tc, int: nEdges,
                            set of int: sc,
                            array[int, int] of int: edges) =
let {
  array[1..nEdges, 1..2] of int: crTEdges = array2d(1..nEdges, 1..2, [ if i = 0 then p else s endif | s,p in sc, i in 0..1 where tc[p,s]]);
} in (crTEdges);
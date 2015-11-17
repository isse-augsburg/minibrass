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

% cr-weighting.mzn --------------
% -------------------------------
% This library offers methods
% to calculate integer weightings
% consistent with constraint 
% relationships.
% -------------------------------

% a utility method that returns
% the dominated soft constraints
% e.g., if c1 <- c2, c1 <- c3, then
% dominees[c1] = {c2,c3}
function array[int] of set of int: 
          getDominees(set of int: softConstraints,
                      array[int, int] of int: edges) = (
             [ {succ | succ in softConstraints where exists(e in index_set_1of2(edges)) 
              (edges[e,1] = succ /\ edges[e,2] = pred)} | pred in SOFTCONSTRAINTS]
 );

% a generic weighting function to assign
% the penalties array; takes a constraint
% relationship as a graph and returns the weight
% switching between SPD and TPD is done by the flag useSPD
function int: weighting(int: s, set of int: softConstraints,
                        array[int, 1..2] of int: edges,
                        bool: useSPD) =
let {
  array[softConstraints] of set of softConstraints: dominees = getDominees(softConstraints, edges);
} in (
  if useSPD then weightingSPD(s, softConstraints, dominees) else
                 weightingTPD(s, softConstraints, dominees) 
  endif
); 

% assigns weights such that the weight of a soft constraint
% is higher than the sum of all its predecessors' joint weight
function int: weightingTPD(int: s, set of int: softConstraints, 
                           array[int] of set of int: dominees) = (
  1 + sum(s_ in dominees[s]) (2*weightingTPD(s_, softConstraints, dominees)-1) 
);

% assigns weights such that the weight of a soft constraint
% is higher than the heaviest of all its predecessors' weights
function int: weightingSPD(int: s, set of int: softConstraints, 
                           array[int] of set of int: dominees) = (
  1 +  ( if dominees[s] = {} then 0 else max(s_ in dominees[s]) (weightingSPD(s_, SOFTCONSTRAINTS, dominees)) endif)
);

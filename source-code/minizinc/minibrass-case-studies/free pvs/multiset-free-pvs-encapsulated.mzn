%% ================================================
%% Free PVS prototype 
%% -------------------
%% Illustrates how we can encode a multiset
%% with Smyth ordering in MiniZinc
%% AS, 2017
%% ================================================

% First, we need an identifier for all elements
% in our underlying set |P| (called "P" here) 
% For this example, the natural ordering over ints
% is used
int: maxP = 2;
set of int: P = 1..maxP;

% This is just a preparation for the case when we have
% a number of soft constraints -- each mapping to
% one multiset over P
int: nScs = 2;              
int: maxOccPerSc = 1; % sets a finite upper bound for every individual soft constraint grading
% i.e., every element of P can appear at most maxOccPerSc times in a multiset 

% The set of possible occurrence in the overall grading is thus bounded as below
set of int: OCCS = 0 .. maxOccPerSc * nScs; % each value can be voted for at most maxPerSc * nScs times, if every soft constraint gets to offer a multiset
set of int: PosOCCS = OCCS diff {0}; % useful for the definition of our witness

include "alldifferent_except_0.mzn";
include "value_precede_chain.mzn"; % for some symmetry breaking 

%% =================================
%% The overall gradings T and U 
%% are encoded multisets over P
array[P] of var OCCS: T;
array[P] of var OCCS: U;
%% =================================

predicate isSmythWorseOrEqual (
              array[P] of var OCCS: T,
              array[P] of var OCCS: U,
          ) = let {
  % the two components of the witness function
  set of int: P0 = {0} union P; % 0 representing no assignment
  
  % we have to split the witness function h : S(U) \to S(T) into 
  % two arrays of decision variables

  % assume h(j,q) = (i,p):
  array[OCCS,P] of var P0: witnessElem;   % witnessElem[j,q] = p 
  array[OCCS,P] of var OCCS: witnessOcc;  % witnessOcc[j,q] = it
  array[OCCS,P] of var 0 .. maxP + (maxOccPerSc * nScs+maxP)*(maxOccPerSc * nScs+maxP+1) div 2: cantoredWitness;
  %array[OCCS,P] of var int: cantoredWitness;
  %constraint forall(i in OCCS, p in P) (cantoredWitness[i,p] = witnessOcc[i,p]*maxP + witnessElem[i,p]);

  constraint forall(i in OCCS, p in P) (cantoredWitness[i,p] = witnessOcc[i,p] + (witnessElem[i,p]+witnessOcc[i,p])*(witnessElem[i,p]+witnessOcc[i,p]+1) / 2); 
  % injectivity (respecting partiality of the witness)
  constraint alldifferent_except_0([cantoredWitness[i,p] | i in OCCS, p in P]);  
  
  constraint value_precede_chain(OCCS, [witnessOcc[i,p] | i in OCCS, p in P]);
  % First, we make sure all (j,q) tuples for occurrences j greater than the 
  % actual number of q elements in U map to non-existence
  constraint forall(q in P, j in OCCS where j > U[q]) ( 
    witnessElem[j,q] = 0 /\ witnessOcc[j,q] = 0
  );

  % Now, for all (j,q) tuples in S(U), they have to map
  % to a (k,p) tuple in S(T) such that p <= q
  constraint forall(q in P, j in PosOCCS where j <= U[q]) 
  % (k,p) = witness(j,q)
  % p = witnessElem[l,q]
  (
    % p must not be 0      and p must be leq than q    
    witnessElem[j,q] != 0 /\ witnessElem[j,q] <= q /\ 
    % k must be between 1 and the actual number of p-occurrences in T
    witnessOcc[j,q] >= 1 /\ witnessOcc[j,q] <= T[witnessElem[j,q]]
  );

  % lastly, we have to assert injectivity of our witness -- only with a decomposition and no global
  %constraint forall(q,q1 in P, j,j1 in PosOCCS where j <= U[q] /\ j1 <= U[q1]) ( 
   % witnessElem[j,q] = witnessElem[j1,q1] /\ witnessOcc[j,q] = witnessOcc[j1,q1] -> j = j1 /\ q = q1 
  %);
} in (
  true
); 

constraint isSmythWorseOrEqual(T,U) ;

constraint exists(i in P) (T[i] != U[i]);

output ["T = \(T) \n" ++
        "U = \(U) \n"];
solve satisfy;

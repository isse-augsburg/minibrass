include "alldifferent_except_0.mzn";

predicate isSmythWorse (
              array[int] of var int: T,
              array[int] of var int: U,
              int: nScs,
              array[int, 1..2] of int: edges,
              int: maxP,
              int: maxPerSc,
              int: maxOccurrences
          ) = let {
  set of int: P = 1..maxP;
  par int: maxOcc = maxPerSc*maxOccurrences;
  set of int: OCCS = 0..maxOcc;
  set of int: PosOCCS = OCCS diff {0};

  int: le = min(index_set_1of2(edges));
  int: ue = max(index_set_1of2(edges));

  array[P] of set of P: lessThanOrEquals = 
    [ {q} union {p | p in P where exists(e in le..ue) 
         (edges[e,1] = p /\ edges[e,2] = q)} |  q in P];

  % the two components of the witness function
  set of int: P0 = {0} union P; % 0 representing no assignment
  
  % we have to split the witness function h : S(U) \to S(T) into 
  % two arrays of decision variables

  % assume h(j,q) = (i,p):
  array[OCCS,P] of var P0: witnessElem;   % witnessElem[j,q] = p 
  array[OCCS,P] of var OCCS: witnessOcc;  % witnessOcc[j,q] = it
  
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
    witnessElem[j,q] != 0 /\ witnessElem[j,q] in lessThanOrEquals[q] /\ 
    % k must be between 1 and the actual number of p-occurrences in T
    witnessOcc[j,q] >= 1 /\ witnessOcc[j,q] <= T[witnessElem[j,q]]
  );

  % lastly, we have to assert injectivity of our witness 
  % literally, we would want that: 

  %constraint forall(q,q1 in P, j,j1 in PosOCCS where j <= U[q] /\ j1 <= U[q1]) ( 
  %  witnessElem[j,q] = witnessElem[j1,q1] /\ witnessOcc[j,q] = witnessOcc[j1,q1] -> j = j1 /\ q = q1 
  %);

  % but that leads to a large decomposition, so we cantorize our 2D witness to 1D and 
  % use alldifferent
  array[OCCS,P] of var 0..maxOcc*maxP + maxP: cantoredWitness;
  constraint forall(i in OCCS, p in P) (cantoredWitness[i,p] = witnessOcc[i,p]*maxP + witnessElem[i,p]);
  
  constraint alldifferent_except_0([cantoredWitness[i,p] | i in OCCS, p in P]);  
  
  % make sure we have inequality
  constraint exists(i in P) (T[i] != U[i]);
} in (
  true
); 

function array[int] of var int: multiset_union(array[int,int] of var int: scGradings,
              int: nScs,
              array[int, 1..2] of int: edges,
              int: maxP,
              int: maxPerSc,
              int: maxOccurrences) =
let {
  set of int: UP = 1..maxP;
  set of int: MOCC = 0..nScs*maxPerSc;
  array[UP] of var MOCC: unioned;
  constraint forall(p in UP) (unioned[p] = sum(i in 1..nScs)(scGradings[i,p]) );
}
in unioned;
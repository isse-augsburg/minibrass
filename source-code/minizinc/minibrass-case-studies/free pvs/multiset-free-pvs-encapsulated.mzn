int: maxP = 2; % maybe nScs for symbolic preference levels or just some parameter
set of int: P = 1..maxP;

int: nScs = 2;              % each of the soft constraints casts a vote 
int: maxPerSc = 1;
set of int: OCCS = 0 .. maxPerSc * nScs; % each value can be voted for at most maxPerSc * nScs times, if every soft constraint gets to offer a multiset
set of int: PosOCCS = OCCS diff {0};

array[P] of var OCCS: T;
array[P] of var OCCS: U;

predicate isWorse(array[int] of var int: T,
                  array[int] of var int: U,
                  set of int: P,
                  set of int: OCCS) = let {
  % the two components of the witness function
  set of int: P0 = {0} union P; % 0 representing no assignment
  
  array[OCCS,P] of var P0: witnessElem;
  array[OCCS,P] of var OCCS: witnessOcc;

  
  constraint forall(q in P, l in OCCS where l > U[q]) ( 
    witnessElem[l,q] = 0 /\ witnessOcc[l,q] = 0
  );

  constraint forall(q in P, l in PosOCCS where l <= U[q]) 
  % (k,p) = witness(j,q)
  % p = witnessElem[l,q]
  (
    witnessElem[l,q] in P /\ witnessElem[l,q] <= q /\ 
    witnessOcc[l,q] >= 1 /\ witnessOcc[l,q] <= T[witnessElem[l,q]]
  );

  constraint forall(q,q1 in P, l,l1 in PosOCCS where l <= U[q] /\ l1 <= U[q1]) ( 
    witnessElem[l,q] = witnessElem[l1,q1] /\ witnessOcc[l,q] = witnessOcc[l1,q1] -> l = l1 /\ q = q1 
);
} in (
  true
); 

constraint isWorse(T,U, P, OCCS) ;

constraint exists(i in P) (T[i] != U[i]);
%constraint isWorse(U,T, OCCS);
% T = \lbag 1,1,2 \rbag
%constraint T = [2,1,0];

% U = \lbag 1,3 \rbag
%constraint U = [1,0,1]; 


%constraint isWorse(T,U);
% we have to decide a witness from the left to the right

output ["T = \(T) \n" ++
        "U = \(U) \n"];
solve satisfy;

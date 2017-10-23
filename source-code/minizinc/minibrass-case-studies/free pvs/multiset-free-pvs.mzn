int: maxP = 2; % maybe nScs for symbolic preference levels or just some parameter
set of int: P = 1..maxP;
set of int: P0 = {0} union P; % 0 representing no assignment

int: nScs = 2;              % each of the soft constraints casts a vote 
int: maxPerSc = 1;
set of int: OCCS = 0 .. maxPerSc * nScs; % each value can be voted for at most maxPerSc * nScs times, if every soft constraint gets to offer a multiset
set of int: PosOCCS = OCCS diff {0};

array[P] of var OCCS: T;
array[P] of var OCCS: U;

% T = \lbag 1,1,2 \rbag
%constraint T = [2,1,0];

% U = \lbag 1,3 \rbag
%constraint U = [1,0,1]; 

% the two components of the witness function
array[OCCS,P] of var P0: witnessElem;
array[OCCS,P] of var OCCS: witnessOcc;

constraint forall(p in P, l in OCCS where l > U[p]) ( 
  witnessElem[l,p] = 0 /\ witnessOcc[l,p] = 0
);

constraint forall(p in P, l in PosOCCS where l <= U[p]) ( 
  witnessElem[l,p] in P /\ witnessElem[l,p] <= p /\ witnessOcc[l,p] >= 1 /\ witnessOcc[l,p] <= T[witnessElem[l,p]]
);

array[OCCS,P] of var int: cantoredWitness;
constraint forall(i in OCCS, p in P) (cantoredWitness[i,p] = witnessOcc[i,p]*maxP + witnessElem[i,p]);
include "alldifferent_except_0.mzn";
constraint alldifferent_except_0([cantoredWitness[i,p] | i in OCCS, p in P]);  

%constraint forall(p,p1 in P, l,l1 in PosOCCS where l <= U[p] /\ l1 <= U[p1]) ( 
%  witnessElem[l,p] = witnessElem[l1,p1] /\ witnessOcc[l,p] = witnessOcc[l1,p1] -> l = l1 /\ p = p1 
%); 

%constraint T = [1, 0];
%constraint U = [2, 1]; 
%predicate isWorse(array[int] of var int: T,
%                  array[int] of var int: U) = let {

%} in (true); 


%constraint isWorse(T,U);
% we have to decide a witness from the left to the right

output ["T = \(T) \n" ++
        "U = \(U) \n" ++ 
        "Witness = \n"] ++
       [  "g(\(l), \(p)) |-> (\(witnessOcc[l,p]), \(witnessElem[l,p]))\n"
       | p in P, l in OCCS 
 ] ++ ["Cantored: \([cantoredWitness[i,p] | i in OCCS, p in P])\n"];
solve satisfy;

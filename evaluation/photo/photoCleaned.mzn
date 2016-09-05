%-----------------------------------------------------------------------------
% Placing people on a photo
%
% Guido Tack
% 2007-02-22
%
% Ported from the Gecode example
%
% Alexander Schiendorfer 
% 2016-03-29; Modified for MiniBrass 
%
%-----------------------------------------------------------------------------

% A group of people wants to take a group photo. Each person can give
% preferences next to whom he or she wants to be placed on the
% photo. The problem to be solved is to find a placement that
% satisfies as many preferences as possible.

include "globals.mzn";

%-----------------------------------------------------------------------------
% Specification

int: n; % n_names;
int: m; % n_prefs;
set of int: PEOPLE = 1..n;
set of int: PREFS = 1..m;

array[PREFS, 0..1] of PEOPLE: prefs; % given as an array of pairs denoting people that would like to stand next to each other

%-----------------------------------------------------------------------------
% Model

array[PEOPLE] of var PEOPLE: pos; % essentially just a permutation of people
var PREFS: satisfies;  % to see if preference p is met

array[PREFS] of var bool: ful;

constraint
  forall (i in PREFS) (
    let {
      int: pa = prefs[i,0],
      int: pb = prefs[i,1]
    } in
    ful[i] = (pos[pb]-pos[pa] == 1 xor pos[pa]-pos[pb] == 1)
  );

constraint
  satisfies = sum (i in PREFS) (bool2int(ful[i])) ;

constraint
  alldifferent(pos);

% Break some symmetry
constraint
  pos[1] < pos[2];

solve :: int_search(pos, first_fail, indomain, complete) 
    maximize satisfies; % maybe try to modify with minimization, see photoMin.mzn

output [
    "pos = ", show(pos), "\n",
    "satisifes = ", show(satisfies), "\n"
];

%-----------------------------------------------------------------------------
%-----------------------------------------------------------------------------

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
include "evaluation-config.mzn";
%-----------------------------------------------------------------------------
% Specification

int: n; % n_names;
int: m; % n_prefs;
set of int: PEOPLE = 1..n;
set of int: PREFS = 1..m;
nScs = m;
%penalties = [1 | i in PREFS];

array[PREFS, 0..1] of PEOPLE: prefs; % given as an array of pairs denoting people that would like to stand next to each other
include "soft_constraints/soft_constraints.mzn";

% include PVS-specific libraries 
include "evaluation-pvs.mzn";

%-----------------------------------------------------------------------------
% Model

array[PEOPLE] of var PEOPLE: pos; % essentially just a permutation of people

%array[PREFS] of var bool: ful;

constraint
  forall (i in PREFS) (
    let {
      int: pa = prefs[i,0],
      int: pb = prefs[i,1]
    } in
    satisfied[i] = (pos[pb]-pos[pa] == 1 xor pos[pa]-pos[pb] == 1)
  );


constraint
  alldifferent(pos);

% Break some symmetry
constraint
  pos[1] < pos[2];


% include search heuristic to be used with model 
ann: custSearch ;%= int_search([penSum], first_fail, indomain_min, complete);
array[int] of var int: lnsVars = pos;

include "evaluation-heuristics.mzn";

include "evaluation-search.mzn";

output [
    "pos = ", show(pos), "\n",
    "penSum = ", show(penSum), "\n",
    "penalties = ", show(penalties), "\n",
    "violatedScs = ", show(violatedScs), "\n",
    "soft constraints = ", show(satisfied), "\n"
];

%-----------------------------------------------------------------------------
%-----------------------------------------------------------------------------

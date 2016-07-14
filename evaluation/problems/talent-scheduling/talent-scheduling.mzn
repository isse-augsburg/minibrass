% originally for SOLVER by Paul Shaw
% modified by Barbara Smith
% converted to MiniZinc by Peter J. Stuckey
%
%

include "globals.mzn";

int: numActors; % number of actors
int: numScenes; % numer of scenes

% -- MiniBrass ----------------------------------------------------------------
include "evaluation-config.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_consistency.mzn";

% include PVS-specific libraries 
include "evaluation-pvs.mzn";

% -----------------------------------------------------------------------------

%-- Types ---------------------------------------------------------------------
set of int: Actors = 1..numActors;
set of int: Scenes = 1..numScenes;

array[Actors,Scenes] of 0..1: ia; % 01 definition of actors in scenes
array[Scenes] of set of Actors: a = 
     [ { j | j in Actors where ia[j,i] == 1} | i in Scenes] ; % actors for each scene
array[Scenes] of int: d; % duration of each scene
array[Actors] of int: c; % cost of each actor


%-- Decision variables --------------------------------------------------------

array[Scenes] of var Scenes: s; % schedule of scenes

%-- Auxilliary variables ------------------------------------------------------

array[Scenes] of var Scenes: slot;  % inverse of scene permutation
array[Actors] of var Scenes: firstSlot; % first slot where actor appears
array[Actors] of var Scenes: lastSlot; % last slot where actor appears
array[Actors] of var int: wait; % onset time

%-- Auxilliary for soft constraints 
int: maxDur = sum(s in Scenes) (d[s]);
set of int: DUR = 0..maxDur;
set of int: ScenesExt = 1..(numScenes+1);

array[ScenesExt] of var DUR: cumDur = [ sum(s_ in 1..sc-1) (d[s[s_]]) | sc in ScenesExt];
array[Actors] of var DUR: firstJob = [ cumDur[firstSlot[a]] | a in Actors];
array[Actors] of var DUR: lastJob = [ cumDur[lastSlot[a] + 1] | a in Actors];

% constraints will be along the lines:

%-- Soft Constraints early and late preferences -------------------------------
int: earliestPrefs;
set of int: EARLY = 1..earliestPrefs;

array[EARLY] of Actors: actorEarl;
array[EARLY] of DUR: durEarl;
array[EARLY] of SOFTCONSTRAINTS: scEarl;

int: latestPrefs;
set of int: LATE = 1..latestPrefs;

array[LATE] of Actors: actorLate;
array[LATE] of DUR: durLate;
array[LATE] of SOFTCONSTRAINTS: scLate;

% constraint firstJob[1] >= 4 <-> satisfied[3];
constraint forall(e in EARLY) (lastJob[ actorEarl[e] ] >= durEarl[e] <-> satisfied[scEarl[e]] );

% constraint lastJob[2] <= 9 <-> satisfied[4];
constraint forall(l in LATE) (lastJob[ actorLate[l] ] <= durLate[l] <-> satisfied[scLate[l]] );

%-- Soft Constraints longest scene at front ------------------------------------

% and to have a longest scene at front ? 
constraint satisfied[1] <-> (d[ s[1] ] = max(sc in Scenes) (d[sc])); 

% latest scheduled should be the shortest
constraint satisfied[2] <-> (d[ s[numScenes] ] = min(sc in Scenes) (d[sc])); 

% and to have beef with colleagues ? 
% define pairs of adversaries :
%  for each adversary pair, post soft constraint meeting said person 
%constraint satisfied[5] = true;
int: adversarials;
set of int: ADV = 1..adversarials;

array[ADV] of Actors: actorAdv;
array[ADV] of Actors: adversaryAdv;
array[ADV] of SOFTCONSTRAINTS: scAdv;

array[int, 1..2] of int: crTempEdges;
crEdges = transClosure(SOFTCONSTRAINTS, crTempEdges);
nCrEdges = max(crEdges);

% constraint meetsAdversary(1, 2, ia, slot,satisfied[5]);

constraint forall(a in ADV) (meetsAdversary(actorAdv[a], adversaryAdv[a], ia, slot, satisfied[ scAdv[a] ]) ); 

var 0..slackedCost: cost;

int: slackedCost;
%constraint cost <= slackedCost;
%-- Predicates ---------------------------------------------------------------

%-- Predicate to capture adversaries 
predicate meetsAdversary(par Actors: actor, par Actors: adversary,
                         array[Actors,Scenes] of 0..1: ia,
                         array[Scenes] of var Scenes: slot, 
                         var bool: sc) =
     let {
        set of Scenes: actorScenes = {s | s in Scenes where ia[actor,s] == 1};
        set of Scenes: advScenes = {s | s in Scenes where ia[adversary,s] == 1};
        set of Scenes: adversAlone = advScenes diff actorScenes;
        set of Scenes: actorAlone = actorScenes diff advScenes;        
     } in 
    sc <-> ( forall(s_a in actorAlone, s_adv in adversAlone)  ( abs(slot[s_a] - slot[s_adv]) > 1 ) );
    %trace("Adversaries (\(actor), \(adversary)): actorAlone \(actorAlone), adversAlone\(adversAlone)", 
    %sc <-> ( forall(s_a in actorAlone, s_adv in adversAlone)  ( abs(slot[s_a] - slot[s_adv]) > 1 ) ) );

%-- Predicate to set up firstSlot and lastSlot

predicate firstlast(Actors: actor, 
                    array[Actors,Scenes] of 0..1: ia,
                    array[Scenes] of var Scenes: slot, 
      	            var Scenes: first, 
                    var Scenes: last) = 
     let {
         int: n = card({ i | i in Scenes where ia[actor,i] == 1 }),
         array[1..n] of var Scenes: position = [ slot[i] | i in Scenes where ia[actor,i] == 1 ]
     } in
         first = min(position) /\
	 last = max(position) /\
	 last - first >= n - 1;




%-- Constraints ---------------------------------------------------------------

constraint inverse(s,slot);

constraint forall(j in Actors)(
	   	 firstlast(j, ia, slot, firstSlot[j], lastSlot[j])
		 );

constraint forall(j in Actors)(
	   	  wait[j] = sum(i in Scenes where ia[j,i] == 0)(
		       	       d[i] * bool2int(firstSlot[j] <= slot[i] /\
			                       slot[i] <= lastSlot[j])
			  ));

%-- Redundant Constraints -----------------------------------------------------

%% implied constraint from Barbara Smith: cost on waiting time
constraint forall(j in Actors)(
	   	  wait[j] >= lastSlot[j] - firstSlot[j] - 
		  	     card({ i | i in Scenes where ia[j,i] == 1 })
		  );

% implied constraints on ordering of scenes
constraint forall(i,j in Scenes where a[i] superset a[j])(
	   let {
	         set of Actors: diffn = a[i] diff a[j]
	   } in 
	     if (card(diffn) == 1) then
	     	forall(k in diffn)(
			 slot[j] < slot[i] -> firstSlot[k] > slot[j]
		)
             else if (card(diffn) == 2) then
	     	  forall(k,l in diffn where k < j)(
		  	 slot[j] < slot[i] -> (firstSlot[k] > slot[j] \/ firstSlot[l] > slot[j])
		  )
	     else
		true
	     endif endif
	  );

%-- calculating the objective function

constraint cost = sum(j in Actors)(
	   	  	sum(i in Scenes)(
		     	   c[j] * d[i] * ia[j,i] %% fixed cost
		        ) +
			c[j] * wait[j]
                  );   

%-- symmetry breaking constraint
constraint s[1] < s[numScenes];

%-- Solving objective and solution output -------------------------------------

% include search heuristic to be used with model 
ann: custSearch = int_search(s, first_fail, indomain, complete);
array[Scenes] of var Scenes: lnsVars = s;

include "evaluation-heuristics.mzn";
include "evaluation-search.mzn";

%solve :: 
%  satisfy;  
%   minimize cost;

output ["\npenSum = \(penSum)\n",
		"\nviolatedScs= \(violatedScs)\n",
       "cost = ", show(cost), ";\ns = ", show(s), 
       ";\nslot = ", show(slot), 
       ";\nfirstSlot = ", show(firstSlot), 
       ";\nlastSlot = ", show(lastSlot), 
       ";\ncumDur = ", show(cumDur),
       ";\nfirstJob = ", show(firstJob), 
       ";\nlastJob = ", show(lastJob), 
       ";\nwait = ", show(wait),
       ";\na = ", show(a),
       ";\n"]; 

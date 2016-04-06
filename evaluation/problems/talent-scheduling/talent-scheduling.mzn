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
% constraint firstJob[1] >= 4;
% constraint lastJob[2] <= 9;

% and to have a longest scene at front ? 

constraint satisfied[1] <-> (d[ s[1] ] = max(sc in Scenes) (d[sc])); 
% latest scheduled should be the shortest
constraint satisfied[2] <-> (d[ s[numScenes] ] = min(sc in Scenes) (d[sc])); 

nScs = 2; % only those two for now 
nCrEdges = 1;
crEdges = [| 2, 1 |]; % earliest is more important 


% and to have beef with colleagues ? 

var 0..slackedCost: cost;

int: slackedCost;
%constraint cost <= slackedCost;
%-- Predicates ---------------------------------------------------------------

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


% only for testing! (should be removed)
%mostImportantFirst = true;
%propagateRedundant = true;

%solve :: 
%  satisfy;  
%   minimize cost;

output ["penSum = \(penSum)\n",
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

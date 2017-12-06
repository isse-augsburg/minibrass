%-----------------------------------------------------------------------------%
% vim: ts=4 sw=4 et wm=0 tw=0
%-----------------------------------------------------------------------------%
% Model example for Multi-Skilled Project Scheduling Problems (MSPSP)
% 
% MSPSP is a variation of the basic resource-constrained project scheduling
% problem. Here, a set of activities must be executed so that the project
% duration is minimised while satisfying all constraints involved. These
% constraints are
% 1) precedence relations between some activities expressing that an activity
% can only be run after its preceding activity's execution is finished,
% 2) skills requirements of activities on workers who have the capability to
% perform the activity, and
% 3) workers availibility, i.e., a worker can perform only one activity in
% each time period.
% % Modified by Alexander Schiendorfer for the MiniBrass evaluation, 2017
%-----------------------------------------------------------------------------%

include "globals.mzn";

% -- MiniBrass ----------------------------------------------------------------
include "evaluation-config.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_consistency.mzn";

% include PVS-specific libraries (always a constraint relationship) 
include "evaluation-pvs.mzn";

array[int, 1..2] of int: crTempEdges;
crEdges = transClosure(SOFTCONSTRAINTS, crTempEdges); 
nCrEdges = max(index_set_1of2(crEdges));

%-----------------------------------------------------------------------------%
% Model parameters.

    % Skills parameters
    %
int: n_skills;
set of int: Skills  = 1 .. n_skills;
int: n_workers;
set of int: Workers = 1 .. n_workers;
array [Workers] of set of Skills: has_skills;

array [Skills] of int: rc = 
    [ card({j | j in Workers where i in has_skills[j]}) | i in Skills ];

    % Task parameters
    %
int: n_tasks;                               % Number of tasks
set of int: Tasks = 1 .. n_tasks;           % Set of tasks
array [Tasks]         of int       : d  ;   % Durations
array [Skills, Tasks] of int       : rr ;   % Resource requirements
array [Tasks]         of set of Tasks: suc;   % Successors

    % Planning horizon
    %
int: tmax = sum(i in Tasks)( d[i] );       % Trivial upper bound
set of int: Times = 0 .. tmax;

%-----------------------------------------------------------------------------%
% Model variables.

array [Tasks] of var Times: s;         % Start times
array [Workers, Tasks] of var bool: w; % Assignment of workers to tasks
Times: maxMakeSpan;                    % maximal makespan should be found by original optimization
var 0..maxMakeSpan: objective;                  % Project duration (makespan)

%-----------------------------------------------------------------------------%
% Constraints.

    % Precedence constraints
    %
constraint
   forall ( i in Tasks, j in suc[i] )
   (
         s[i] + d[i] <= s[j]
   );

    % Skills constraints
    %
constraint
    forall ( i in Tasks )
    (
        let {
            set of int: TWorkers = 
                { j | j in Workers where 
                            exists(k in has_skills[j])(rr[k, i] > 0)
                }
        } in (
            forall ( k in Skills where rr[k, i] > 0 )
            (
                sum(j in TWorkers where k in has_skills[j])
                (
                    bool2int(w[j, i])
                ) >= rr[k, i]
            )
        /\  forall ( j in Workers where not(j in TWorkers) )
            (
                w[j, i] = false
            )
        )
    );

    % Redundant non-overlapping constraints for the workers
    %
constraint
    forall ( j in Workers )
    (
        let {
            set of int: WTasks =
                { i | i in Tasks where 
                            exists(k in has_skills[j])(rr[k, i] > 0)
                }
        } in (
            if card(WTasks) > 1 /\ propagateWeights then
                cumulative(
                    [ s[i]              | i in WTasks ],
                    [ d[i]              | i in WTasks ],
                    [ bool2int(w[j, i]) | i in WTasks ],
                    1
                )
            else
                true
            endif
        )
    );

    % Redundant non-overlapping constraints
    %
constraint
	forall ( 
		i, j in Tasks
	where 
		i < j /\ not(j in suc[i]) /\ not(i in suc[j])
	)(
        if exists( k in Skills )( rr[k,i] + rr[k,j] > rc[k] )  then
            let { var bool: before } in (
		        (     before  -> s[i] + d[i] <= s[j] )
            /\  ( not(before) -> s[j] + d[j] <= s[i] )
            )
        else
            true
        endif
	);

    % Redudant cumulative resource constraints
    %
constraint
    forall ( k in Skills )
    (
        let { 
            set of int: RTasks = { i | i in Tasks where rr[k, i] > 0 },
            int: sum_rr = sum(i in RTasks)( rr[k,i] )
        } in (
            if card(RTasks) > 1 /\ sum_rr > rc[k] then
                cumulative(
                    [ s[i]    | i in RTasks ],
                    [ d[i]    | i in RTasks ],
                    [ rr[k,i] | i in RTasks ],
                    rc[k]
                )
            else
                true
            endif
        )
    );

    % Makespan constraints
    %
constraint
   forall ( i in Tasks where suc[i] == {} )
   (
      s[i] + d[i] <= objective
   );

%-----------------------------------------------------------------------------%
% Soft Constraints.
nScs = togethers + notTogethers + nDesireds + nAvoideds ;

% 1.) Work with some other agent
array[int] of set of Workers: workTogether;
array[1..togethers] of SOFTCONSTRAINTS: scWorkTogether;

int: togethers = max(index_set(workTogether));

constraint forall(i in 1..togethers)(satisfied[scWorkTogether[i]] <-> exists(t in Tasks)(forall(w_ in workTogether[i]) (w[w_,t] ) ) );

% 2.) Do not work with other agent
array[int] of set of Workers: workNotTogether;
array[1..notTogethers] of SOFTCONSTRAINTS: scWorkNotTogether;

int: notTogethers = max(index_set(workNotTogether));

constraint forall(i in 1..notTogethers)(satisfied[scWorkNotTogether[i]] <-> 
forall (t in Tasks)( sum(w_ in workNotTogether[i]) (bool2int(w[w_,t])) <= card(workNotTogether[i]) ) );

% 3.) Work on certain task preferably
array[int] of Workers: desiredTaskWorkers;
array[1..nDesireds] of Tasks: desiredTasks;
array[1..nDesireds] of SOFTCONSTRAINTS: scDesireds;

int: nDesireds = max(index_set(desiredTaskWorkers ));
constraint forall(i in 1..nDesireds) (satisfied[scDesireds[i]] <-> w[desiredTaskWorkers[i],desiredTasks[i]] );

% 4.) Do not work on a certain task preferably    
array[int] of Workers: avoidedTaskWorkers;
array[1..nAvoideds] of Tasks: avoidedTasks;
array[1..nAvoideds] of SOFTCONSTRAINTS: scAvoideds;

int: nAvoideds = max(index_set(avoidedTaskWorkers ));

constraint forall(i in 1..nAvoideds) (satisfied[scAvoideds[i]] <-> not w[avoidedTaskWorkers[i],avoidedTasks[i]] );

%-----------------------------------------------------------------------------%
% Objective.

ann: custSearch = seq_search([
        int_search(s, smallest, indomain_split, complete),
        bool_search([w[i,j] | i in Workers, j in Tasks], input_order, 
            indomain_max, complete),
        int_search([objective], input_order, indomain_min, complete)
    ]);

% a bit of a workaround here for lns (does not work with boolean vars, as of now)
% make lns-vars a flattened out version of w
% make sure they are instantiated, though
% array [Workers, Tasks] of var bool: w; % Assignment of workers to tasks
int: flatWLen = n_workers * n_tasks;
array[1..flatWLen] of var 0..1: lnsVars;

constraint forall(w_ in Workers, t in Tasks)(w[w_,t] = lnsVars[ (w_ - 1) * n_tasks + t]  ); 
%   1  2
% 1 a  b
% 2 c  d
% 3 e  f

%     1 2 3 4 5 6    
% --> a b c d e f 
%solve
%  :: custSearch    
%    minimize penSum;

include "evaluation-heuristics.mzn";
include "evaluation-search.mzn";

%-----------------------------------------------------------------------------%
% Output.

output [
    "% mspsp\n",
    "penSum = \(penSum)\n",
    "penalties = \(penalties)\n",
    "violatedScs = \(violatedScs)\n",
    "lnsVars = \(lnsVars)\n",
%    "s = "         ++ show( s         ) ++ ";\n",
%    "w = "         ++ show( w         ) ++ ";\n",
    "objective = " ++ show( objective ) ++ ";\n"
];

%-----------------------------------------------------------------------------%
%%% EOF %%%


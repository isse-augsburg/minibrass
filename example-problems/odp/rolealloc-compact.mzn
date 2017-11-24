% ==========================================
% rolealloc-compact.mzn
% ---------------------
% Encodes the role allocation problem faced
% in the adaptive production cell as a 
% rather compact constraint model
% -> requires some simple postprocessing
%    to get an object graph in the ODP
%
% Core idea is to encode roles as "role segments"
% where each role segment can be either
%  *) an applied capability
%  *) a transportation action (of a cart)

/* Example usage 
task

   D   D  I     T  T

capabilities: r1 -> {D,I}, 
              r2 -> {I,T}
              c1, c2 -> {}

ROLE_SEGMENTS: 
   1   2  3  4  5  6                 
  R1  R1 R1 C1 R2 R2 
 
POST
0  1  2  3  3   4  5

CAP APPLIED
   1  1  1  0   1  1

output:

roleSegments = [1,1,1,3,2,2]

-> ) contiguous sequences of the same
agent id shall be collected into one role 
-> ) capToApp is simply the union of the capabilities
     met along the way
*/ 

% ISSE Augsburg, 2016
% ==========================================

include "globals.mzn"; % minizinc globals libary


% ----------------------------------------
% types
% ----------------------------------------

% replaces enumeration type
int: D = 1; int: I = 2; int: T = 3;
set of int: CAP = {D, I, T};
% only for the output 
array[CAP] of string: labels = ["D","I","T"];

% ----------------------------------------
% input data 
% ----------------------------------------

% task specification
array[int] of CAP: task;
set of int: TASK_IND = index_set(task);
set of int: TASK_IND_0 = TASK_IND union {0}; % includes the zero for the first pointer "incoming port from bottom"

% agent specification
int: noAgents;
set of int: AGENT = 1..noAgents;
array[AGENT] of set of CAP: capabilities;
array[AGENT] of bool: isCart;

% connectivity (for now, any cart may connect any robots and that's the only option for mobility)
array[AGENT,AGENT] of bool: isConnected = array2d(AGENT,AGENT, [ isCart[a1] xor isCart[a2] | a1 in AGENT, a2 in AGENT]);

% ----------------------------------------
% decision variables  
% ----------------------------------------

% allocated role segments  
% should be as few as possible, should in principle be bounded by the number of capabilities to apply (including movement tasks) 

int: maxRoleSegments = 2*max(TASK_IND); % robot cart robot cart ... 
set of int: ROLE_SEGMENTS = 1..maxRoleSegments; 
set of int: ROLE_SEGMENTS_0 = ROLE_SEGMENTS union {0};
var ROLE_SEGMENTS: lastRoleSegment; % to be decided by the solver, after that we do not care about the rest

% postcondition
array[ROLE_SEGMENTS_0] of var TASK_IND_0: poststate; % encoded as "processed_until"
constraint increasing(poststate); % monotonically increasing series
constraint poststate[0] = 0 /\ poststate[lastRoleSegment] = max(TASK_IND); % fixed values

array[ROLE_SEGMENTS] of var AGENT: agent;
array[ROLE_SEGMENTS] of var 0..1: capApplied;
% better viewpoint for capabilities constraint
array[TASK_IND] of var AGENT: taskAssigned;

% ----------------------------------------
% constraints ensuring valid transitions 
% for role segments
% ----------------------------------------

constraint forall(r in ROLE_SEGMENTS) (poststate[r] = poststate[r-1] + capApplied[r]);
% channeling role segments viewpoint and taskAssigned viewpoint
constraint forall(r in ROLE_SEGMENTS) (
  capApplied[r] = 1 -> (taskAssigned[poststate[r]] = agent[r])
);

% in every role, the capabilities to apply are a subset of the capabilities of the agent that is assigned to said role
% easier to express in the taskAssigned viewpoint
constraint forall(s in TASK_IND) (task[s] in capabilities[taskAssigned[s]]);

% connect adjacent role segments of different agents 
constraint forall(r in 1..lastRoleSegment-1) (agent[r] != agent[r+1] -> isConnected[agent[r], agent[r+1]]);

% ----------------------------------------
% objective function (make plans short)
% ----------------------------------------

solve 
% prioritize shorter plans (domain-specific search heuristic)
:: int_search([lastRoleSegment], input_order,indomain_min,complete)
minimize lastRoleSegment;

output [
"agents       = \([agent[i] | i in 1..lastRoleSegment] )\n"++
"capApplied   = \([capApplied[i] | i in 1..lastRoleSegment] )\n"++
"workedOn     = \([if capApplied[i] = 1 then poststate[i] else 0 endif | i in 1..lastRoleSegment] )\n"
];
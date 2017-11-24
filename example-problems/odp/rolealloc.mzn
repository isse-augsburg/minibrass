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
array[int] of CAP: task = [D,I,T];
set of int: TASK_IND = index_set(task);
set of int: TASK_IND_0 = TASK_IND union {0}; % includes the zero for the first pointer "incoming port from bottom"

% agent specification
int: noAgents = 4;
set of int: AGENT = 1..noAgents;
array[AGENT] of set of CAP: capabilities = [{D,I}, {I,T}, {}, {}];
array[AGENT] of bool: isCart = [false, false, true, true];

% connectivity (for now, any cart may connect any robots and that's the only option for mobility)
int: bot = 0;
set of int: PORT = AGENT union {bot};
array[AGENT,AGENT] of bool: isConnected = array2d(AGENT,AGENT, [ isCart[a1] xor isCart[a2] | a1 in AGENT, a2 in AGENT]);

%array[TASK_IND] of var ROBOT: assigned; 
%constraint forall(t in TASK_IND) (task[t] in roleAlloc[ assigned[t] ]  );

% allocated roles 
% should be as few as possible, should in principle be bounded by the number of capabilities to apply (including movement tasks) 

int: maxRoles = 2*max(TASK_IND); % robot cart robot cart ... 
set of int: ROLES = 1..maxRoles; 
var ROLES: lastRole; % to be decided by the solver, after that we do not care about the rest
% ----------------------------------------
% decision variables
% ----------------------------------------

array[ROLES] of var AGENT: agent;

% precondition
array[ROLES] of var PORT: preport;
array[ROLES] of var TASK_IND_0: prestate; % encoded as "processed_until"
constraint increasing(prestate); % monotonically increasing series
constraint preport[1] = bot /\ prestate[1] = 0; % fixed values 

% postcondition
array[ROLES] of var PORT: postport;
array[ROLES] of var TASK_IND_0: poststate; % encoded as "processed_until"
constraint increasing(poststate); % monotonically increasing series
constraint postport[lastRole] = bot /\ poststate[lastRole] = max(TASK_IND); % for the last state, we should be finished completely with the task

% cap to apply 
array[ROLES] of var set of CAP: capToApp;

%constraint forall(r in ROLES) (appliedTasks[r] = prestate[r]..poststate[r] );
% task assigned (inverse mapping) 
array[TASK_IND] of var AGENT: taskAssigned;

% in every role, the capabilities to apply are a subset of the capabilities of the agent that is assigned to said role
constraint forall(r in ROLES) (capToApp[r] subset capabilities[agent[r]]) ;
constraint forall(s in TASK_IND) (task[s] in capabilities[taskAssigned[s]]);
%constraint taskAssigned[2] = 2 /\ taskAssigned[1] = 1;
% valid transitions between pre- and postconditions 
% -------------------------------------------------
% monotonicity 
constraint forall(r in ROLES) (
  poststate[r] >= prestate[r] 
);

%constraint taskAssigned=[1,2,2];

% continuity 
constraint forall(r in 1..lastRole-1) (
  prestate[r+1] = poststate[r] 
);

% cap to app links states 
constraint forall(r in ROLES, s in TASK_IND) (s in prestate[r]+1..poststate[r] -> (taskAssigned[s] = agent[r] /\ task[s] in capToApp[r]));

% connect cap to app
constraint forall(r in ROLES, c in capToApp[r]) (exists(s in prestate[r]+1..poststate[r]) (task[s] = c) );
constraint forall(r in 1..lastRole-1) (agent[r] != agent[r+1] -> isConnected[agent[r], agent[r+1]] /\ postport[r] = agent[r+1] /\ preport[r+1] = agent[r]);


solve minimize lastRole;

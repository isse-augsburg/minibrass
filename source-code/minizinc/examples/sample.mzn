% now some literals for names:
n = 3;
int: johnny = 1;
int: carl = 2;
int: frank = 3;

m = 2;
int: kuka = 1;
int: msg = 2;

% some user defined preferences
 
% johnny kuka >> msg
int: jKuka = 1; % we must use ints as identifiers, I'm afraid
int: jMsg = 2; 

constraint worksAt[johnny] = kuka <-> satisfied[jKuka]; 
constraint worksAt[johnny] = msg <-> satisfied[jMsg];

% carl msg >> kuka

% frank has no preferences

% kuka wants johnny >> carl >> frank

% msg wants carl >> johnny ~ frank

nScs = 2; % two soft constraints so far
nCrEdges = 1; % only one true preference 
% johnny being at msg points at (is less important than) johnny being at msg
crEdges = [| jMsg, jKuka |];

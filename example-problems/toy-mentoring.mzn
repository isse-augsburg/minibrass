% now some literals for names:
n = 4;
int: brenner = 1; % brenner
int: teufel = 2;
int: fennek = 3;
int: werewolf = 4;

m = 3;
int: cupgainini = 1;
int: aps = 2;
int: junedied = 3;

% -------------------------------------------------------
 
% Tony Teufel

int: teufJuned = 1;
int: teufCap = 2;
int: teufaps = 3;

constraint worksAt[teufel] = junedied <-> satisfied[teufJuned];
constraint worksAt[teufel] = cupgainini <-> satisfied[teufCap];
constraint worksAt[teufel] = aps <-> satisfied[teufaps];

% -------------------------------------------------------
 
% Gerhard Brenner

int: breJuned = 4;
int: breCap = 5;
int: breaps = 6;

constraint worksAt[brenner] = junedied <-> satisfied[breJuned];
constraint worksAt[brenner] = cupgainini <-> satisfied[breCap];
constraint worksAt[brenner] = aps <-> satisfied[breaps];

% -------------------------------------------------------
 
% Logan Werewolf
int: wolJuned = 7;
int: wolaps = 8;

constraint worksAt[werewolf] = junedied <-> satisfied[wolJuned];
constraint worksAt[werewolf] = aps <-> satisfied[wolaps];

% -------------------------------------------------------
 
% Marc Fennek 

% has two Equivalence classes ; leave bad as hard for now 

int: fenFavs = 9;
int: fenBad = 10;

constraint worksAt[fennek] in {junedied, aps}  <-> satisfied[fenFavs];
constraint worksAt[fennek] in {cupgainini} <-> satisfied[fenBad];

% make the constraint strict
%constraint not satisfied[fenBad];


%% and now for the companies
%% =======================================================

% -------------------------------------------------------
% junedied
int: junedFen = 11;
int: junedTeuf = 12;

constraint worksAt[fennek] = junedied <-> satisfied[junedFen];
constraint worksAt[teufel] = junedied <-> satisfied[junedTeuf];

% -------------------------------------------------------
% cupgainini

% model this as a fully connected bipartite graph 
int: cupGFen = 13; int: cupGTeuf = 14; % prio A
int: cupGWol = 15; int: cupGBre = 16;    % prio B

constraint worksAt[fennek] = cupgainini <-> satisfied[cupGFen];
constraint worksAt[brenner] = cupgainini <-> satisfied[cupGBre];
constraint worksAt[werewolf] = cupgainini <-> satisfied[cupGWol];
constraint worksAt[teufel] = cupgainini <-> satisfied[cupGTeuf];

% -------------------------------------------------------
% APS

% has no preferences regarding the students 

nScs = 16;
lastStudentPref = 10;

nCrEdges = 11; 

% here, the actual ranking, i.e. prioritization 
% of the preferences (soft constraints) takes place
crEdges = [| breaps, breJuned | breJuned, breCap
           | fenBad, fenFavs 
           | wolaps, wolJuned
           | teufaps, teufCap | teufCap, teufJuned 
           | junedFen, junedTeuf
           | cupGWol, cupGFen | cupGWol, cupGTeuf | cupGBre, cupGFen | cupGBre, cupGTeuf
 |];


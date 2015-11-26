% now some literals for names:
n = 16;
int: brunner = 1;
int: engel = 2;
int: schnappinger = 3;
int: woehrle = 4;
int: cimander = 5;
int: schreier = 6;
int: vogel = 7;
int: hartmond = 8;
int: riedmann = 9;
int: fritsch = 10;
int: schmidt = 11;
int: wurzer = 12;
int: kindelsberger = 13;
int: jenik = 14;
int: weber = 15;
int: persa = 16;

m = 7;
int: capgemini = 1;
int: msg = 2;
int: maiborn = 3;
int: sap = 4;
int: oracle = 5;
int: siemens = 6;
int: zuehlke = 7;
% some user defined preferences
 
% raul persa 
int: pOracle = 1;
int: pSap = 2;
int: pMsg = 3;
int: pSiem = 4;
int: pMaib = 5;

constraint worksAt[persa] = oracle <-> satisfied[pOracle];
constraint worksAt[persa] = sap <-> satisfied[pSap];
constraint worksAt[persa] = msg <-> satisfied[pMsg];
constraint worksAt[persa] = siemens <-> satisfied[pSiem];
constraint worksAt[persa] = maiborn <-> satisfied[pMaib];

% -------------------------------------------------------
 
% michael schreier 
int: schrMaib = 6;
int: schrMsg = 7;
int: schrOracle = 8;

constraint worksAt[schreier] = maiborn <-> satisfied[schrMaib];
constraint worksAt[schreier] = msg <-> satisfied[schrMsg];
constraint worksAt[schreier] = oracle <-> satisfied[schrOracle];

% -------------------------------------------------------
 
% Thomas Weber
int: weMaib = 9;
int: weMsg = 10;
int: weOracle = 11;
int: weSiem = 12;
int: weSap = 13;
int: weZuehlke = 14;
int: weCapg = 15;

constraint worksAt[weber] = maiborn <-> satisfied[weMaib];
constraint worksAt[weber] = msg <-> satisfied[weMsg];
constraint worksAt[weber] = oracle <-> satisfied[weOracle];
constraint worksAt[weber] = siemens <-> satisfied[weSiem];
constraint worksAt[weber] = sap <-> satisfied[weSap];
constraint worksAt[weber] = zuehlke <-> satisfied[weZuehlke];
constraint worksAt[weber] = capgemini <-> satisfied[weCapg];

% -------------------------------------------------------
 
% Stefan Cimander
int: cimMaib = 16;
int: cimSiem = 17;
int: cimSap = 18;

constraint worksAt[cimander] = maiborn <-> satisfied[cimMaib];
constraint worksAt[cimander] = siemens <-> satisfied[cimSiem];
constraint worksAt[cimander] = sap <-> satisfied[cimSap];

% -------------------------------------------------------
 
% Thomas Engel

int: engMaib = 19;
int: engCap = 20;
int: engZuehlke = 21;

constraint worksAt[engel] = maiborn <-> satisfied[engMaib];
constraint worksAt[engel] = capgemini <-> satisfied[engCap];
constraint worksAt[engel] = zuehlke <-> satisfied[engZuehlke];

% -------------------------------------------------------
 
% Gernot Brunner

int: bruMaib = 22;
int: bruCap = 23;
int: bruSiem = 24;

constraint worksAt[brunner] = maiborn <-> satisfied[bruMaib];
constraint worksAt[brunner] = capgemini <-> satisfied[bruCap];
constraint worksAt[brunner] = siemens <-> satisfied[bruSiem];

% -------------------------------------------------------
 
% Philipp Riedmann
int: riMaib = 25;
int: riMsg = 26;
int: riOracle = 27;
int: riSiem = 28;
int: riSap = 29;
int: riZuehlke = 30;
int: riCapg = 31;

constraint worksAt[riedmann] = maiborn <-> satisfied[riMaib];
constraint worksAt[riedmann] = msg <-> satisfied[riMsg];
constraint worksAt[riedmann] = oracle <-> satisfied[riOracle];
constraint worksAt[riedmann] = siemens <-> satisfied[riSiem];
constraint worksAt[riedmann] = sap <-> satisfied[riSap];
constraint worksAt[riedmann] = zuehlke <-> satisfied[riZuehlke];
constraint worksAt[riedmann] = capgemini <-> satisfied[riCapg];

% -------------------------------------------------------
 
% Julia Kindelsberger

int: kinOracle = 32;
int: kinSap = 33;
int: kinSiem = 34;

constraint worksAt[kindelsberger] = oracle <-> satisfied[kinOracle];
constraint worksAt[kindelsberger] = sap <-> satisfied[kinSap];
constraint worksAt[kindelsberger] = siemens <-> satisfied[kinSiem];

% -------------------------------------------------------
 
% Jens Wöhrle
int: woeMaib = 35;
int: woeMsg = 36;
int: woeOracle = 37;
int: woeSap = 38;
int: woeZuehlke = 39;
int: woeCapg = 40;

constraint worksAt[woehrle] = maiborn <-> satisfied[woeMaib];
constraint worksAt[woehrle] = msg <-> satisfied[woeMsg];
constraint worksAt[woehrle] = oracle <-> satisfied[woeOracle];
constraint worksAt[woehrle] = sap <-> satisfied[woeSap];
constraint worksAt[woehrle] = zuehlke <-> satisfied[woeZuehlke];
constraint worksAt[woehrle] = capgemini <-> satisfied[woeCapg];

% this is by exception a *hard* constraint
constraint worksAt[woehrle] != siemens;

% -------------------------------------------------------
 
% Tabea Schmidt

int: schmOracle = 41;
int: schmSap = 42;
int: schmSiem = 43;
int: schmMaib = 44;

constraint worksAt[schmidt] = oracle <-> satisfied[schmOracle];
constraint worksAt[schmidt] = sap <-> satisfied[schmSap];
constraint worksAt[schmidt] = siemens <-> satisfied[schmSiem];
constraint worksAt[schmidt] = maiborn <-> satisfied[schmMaib];

nScs = 44; % two soft constraints so far
nCrEdges = 34; % only one true preference 
% johnny being at msg points at (is less important than) johnny being at msg
crEdges = [| pMaib, pSiem | pSiem, pMsg | pMsg, pSap | pSap, pOracle
           | schrOracle, schrMsg | schrMsg, schrMaib 
           | weCapg, weZuehlke | weZuehlke, weSap | weSap, weSiem | weSiem, weOracle | weOracle, weMsg | weMsg, weMaib
           | cimSap, cimSiem | cimSiem, cimMaib
           | engZuehlke, engCap | engCap, engMaib
           | bruSiem, bruMaib | bruMaib, bruCap
           | riSiem, riZuehlke | riZuehlke, riCapg | riCapg, riMsg | riMsg, riSap | riSap, riMaib | riMaib, riOracle
           | kinOracle, kinSap | kinSap, kinSiem
           | woeZuehlke, woeCapg | woeCapg, woeOracle | woeOracle, woeSap | woeSap, woeMsg | woeMsg, woeMaib
           | schmMaib, schmSap | schmSap, schmOracle | schmOracle, schmSiem

 |]

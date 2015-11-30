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
% first 2nd streak
% [1, 1, 4, 3, 3, 2, 2, 3, 5, 7, 6, 6, 6, 5, 2, 5]
% current optimum
% [1, 1, 1, 4, 3, 2, 2, 3, 5, 7, 6, 6, 6, 5, 3, 5]
%  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16
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

% -------------------------------------------------------
 
% Markus Schnappinger 

% has three Equivalence classes ; leave third as hard for now 

int: schnFavs = 45;
int: schnOK = 46;
int: schnBad = 47;

constraint worksAt[schnappinger] in {maiborn, capgemini}  <-> satisfied[schnFavs];
constraint worksAt[schnappinger] in {siemens,msg,zuehlke} <-> satisfied[schnOK];
constraint worksAt[schnappinger] in {sap,oracle} <-> satisfied[schnBad];

% make the constraint strict
constraint not satisfied[schnBad];

% -------------------------------------------------------
 
% Martin Hartmond
int: harMaib = 48;
int: harMsg = 49;
int: harOracle = 50;
int: harSiem = 51;
int: harSap = 52;
int: harZuehlke = 53;
int: harCapg = 54;

constraint worksAt[hartmond] = maiborn <-> satisfied[harMaib];
constraint worksAt[hartmond] = msg <-> satisfied[harMsg];
constraint worksAt[hartmond] = oracle <-> satisfied[harOracle];
constraint worksAt[hartmond] = siemens <-> satisfied[harSiem];
constraint worksAt[hartmond] = sap <-> satisfied[harSap];
constraint worksAt[hartmond] = zuehlke <-> satisfied[harZuehlke];
constraint worksAt[hartmond] = capgemini <-> satisfied[harCapg];

% -------------------------------------------------------
 
% Lukas Vogel

int: vogMaib = 55;
int: vogMsg = 56;
int: vogOracle = 57;
int: vogSiem = 58;
int: vogSap = 59;
int: vogZuehlke = 60;
int: vogCapg = 61;

constraint worksAt[vogel] = maiborn <-> satisfied[vogMaib];
constraint worksAt[vogel] = msg <-> satisfied[vogMsg];
constraint worksAt[vogel] = oracle <-> satisfied[vogOracle];
constraint worksAt[vogel] = siemens <-> satisfied[vogSiem];
constraint worksAt[vogel] = sap <-> satisfied[vogSap];
constraint worksAt[vogel] = zuehlke <-> satisfied[vogZuehlke];
constraint worksAt[vogel] = capgemini <-> satisfied[vogCapg];

% -------------------------------------------------------
 
% Martin Wurzer

int: wurMaib = 62;
int: wurMsg = 63;
int: wurOracle = 64;
int: wurSiem = 65;
int: wurSap = 66;
int: wurZuehlke = 67;
int: wurCapg = 68;

constraint worksAt[wurzer] = maiborn <-> satisfied[wurMaib];
constraint worksAt[wurzer] = msg <-> satisfied[wurMsg];
constraint worksAt[wurzer] = oracle <-> satisfied[wurOracle];
constraint worksAt[wurzer] = siemens <-> satisfied[wurSiem];
constraint worksAt[wurzer] = sap <-> satisfied[wurSap];
constraint worksAt[wurzer] = zuehlke <-> satisfied[wurZuehlke];
constraint worksAt[wurzer] = capgemini <-> satisfied[wurCapg];

% -------------------------------------------------------
 
% Stefan Fritsch

int: friMaib = 69;
int: friMsg = 70;
int: friOracle = 71;
int: friSiem = 72;
int: friSap = 73;
int: friZuehlke = 74;
int: friCapg = 75;

constraint worksAt[fritsch] = maiborn <-> satisfied[friMaib];
constraint worksAt[fritsch] = msg <-> satisfied[friMsg];
constraint worksAt[fritsch] = oracle <-> satisfied[friOracle];
constraint worksAt[fritsch] = siemens <-> satisfied[friSiem];
constraint worksAt[fritsch] = sap <-> satisfied[friSap];
constraint worksAt[fritsch] = zuehlke <-> satisfied[friZuehlke];
constraint worksAt[fritsch] = capgemini <-> satisfied[friCapg];

% -------------------------------------------------------
 
% Benedikt Jenik

int: jenOracle = 76;
int: jenSap = 77;

constraint worksAt[jenik] = oracle  <-> satisfied[jenOracle];
constraint worksAt[jenik] = sap <-> satisfied[jenSap];

%% and now for the companies
%% =======================================================

% -------------------------------------------------------
% MaibornWolff
int: maibKind = 78;
int: maibJen = 79;
int: maibWoe = 80;
int: maibCim = 81;
int: maibSchn = 82;
int: maibEng = 83;

constraint worksAt[kindelsberger] = maiborn <-> satisfied[maibKind];
constraint worksAt[jenik] = maiborn <-> satisfied[maibJen];
constraint worksAt[woehrle] = maiborn <-> satisfied[maibWoe];
constraint worksAt[cimander] = maiborn <-> satisfied[maibCim];
constraint worksAt[schnappinger] = maiborn <-> satisfied[maibSchn];
constraint worksAt[engel] = maiborn <-> satisfied[maibEng];

% -------------------------------------------------------
% Oracle
int: oracKind = 84;
int: oracJen = 85;
int: oracWoe = 86;
int: oracRie = 87;
int: oracSchm = 88;
int: oracWeb = 89;
int: oracVog = 90;
int: oracPers = 91;

constraint worksAt[kindelsberger] = oracle <-> satisfied[oracKind];
constraint worksAt[jenik] = oracle <-> satisfied[oracJen];
constraint worksAt[woehrle] = oracle <-> satisfied[oracWoe];
constraint worksAt[riedmann] = oracle <-> satisfied[oracRie];
constraint worksAt[weber] = oracle <-> satisfied[oracWeb];
constraint worksAt[schmidt] = oracle <-> satisfied[oracSchm];
constraint worksAt[persa] = oracle <-> satisfied[oracPers];
constraint worksAt[oracVog] = oracle <-> satisfied[oracVog];

% -------------------------------------------------------
% CapGemini

% model this as a fully connected bipartite graph 
int: capGSchnapp = 92;
int: capGEng = 93;
int: capGJen = 94;
int: capGWurz = 95;
int: capGKind = 96;
int: capGPers = 97;

constraint worksAt[kindelsberger] = capgemini <-> satisfied[capGKind];
constraint worksAt[jenik] = capgemini <-> satisfied[capGJen];
constraint worksAt[schnappinger] = capgemini <-> satisfied[capGSchnapp];
constraint worksAt[wurzer] = capgemini <-> satisfied[capGWurz];
constraint worksAt[persa] = capgemini <-> satisfied[capGPers];
constraint worksAt[engel] = capgemini <-> satisfied[capGEng];

% -------------------------------------------------------
% SAP

int: sapJen = 98;
int: sapWoe = 99;
int: sapKind = 100;
int: sapVog = 101;
int: sapSchnapp = 102;
int: sapRie = 103;
int: sapCim = 104;

constraint worksAt[kindelsberger] = sap <-> satisfied[sapKind];
constraint worksAt[jenik] = sap <-> satisfied[sapJen];
constraint worksAt[schnappinger] = sap <-> satisfied[sapSchnapp];
constraint worksAt[cimander] = sap  <-> satisfied[sapCim];
constraint worksAt[woehrle] = sap <-> satisfied[sapWoe];
constraint worksAt[riedmann] = sap <-> satisfied[sapRie];
constraint worksAt[vogel] = sap <-> satisfied[sapVog];

% -------------------------------------------------------
% Siemens

int: sieEng = 105;
int: sieFri = 106;
int: sieKind = 107;
int: sieVog = 108;
int: sieSchnapp = 109;
int: sieRie = 110;
int: sieWur = 111;

constraint worksAt[kindelsberger] = siemens <-> satisfied[sieKind];
constraint worksAt[engel] = siemens <-> satisfied[sieEng];
constraint worksAt[schnappinger] = siemens <-> satisfied[sieSchnapp];
constraint worksAt[fritsch] = siemens <-> satisfied[sieFri];
constraint worksAt[wurzer] = siemens <-> satisfied[sieWur];
constraint worksAt[riedmann] = siemens <-> satisfied[sieRie];
constraint worksAt[vogel] = siemens <-> satisfied[sieVog];

% -------------------------------------------------------
% Zuehlke

int: zueJen = 112;
int: zueSchm = 113;
int: zueWeb = 114;

constraint worksAt[jenik] = zuehlke <-> satisfied[zueJen];
constraint worksAt[schmidt] = zuehlke <-> satisfied[zueSchm];
constraint worksAt[weber] = zuehlke <-> satisfied[zueWeb];

nScs = 114;
lastStudentPref = 77;

include "true-edges.mzn";
%include "toy-edges.mzn"; % for better testing



% worksAt = [1, 6, 6, 4, 3, 3, 2, 3, 5, 6, 7, 3, 6, 5, 5, 5], penalty: 265
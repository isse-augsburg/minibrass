% ===============================================
% Generated code from MiniBrass, do not modify!
include "soft_constraints/mbr_types/relation-type.mzn";

% ---------------------------------------------------
% Overall exported predicate (and objective in case of atomic top-level PVS) : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postGetBetter_MBR_VOT_1();
function ann:  postNotGetWorse() = mbr_postNotGetWorse_MBR_VOT_1();
var 0..4: topLevelObjective;
constraint topLevelObjective = mbr_overall_MBR_VOT_1;

% ---------------------------------------------------
%   PVS ÖMüllärß
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_OEMuellaerss = 1; 
int : mbr_k_OEMuellaerss = 3; 
set of 1..mbr_k_OEMuellaerss : mbr_approvedSet_OEMuellaerss = {1,2}; 

% Decision variables: 
var bool: mbr_overall_OEMuellaerss;
array[1..mbr_nScs_OEMuellaerss] of var bool: mbr_valuations_OEMuellaerss;
bool: mbr_top_OEMuellaerss = true;
constraint mbr_overall_OEMuellaerss = and (mbr_valuations_OEMuellaerss,mbr_nScs_OEMuellaerss, mbr_k_OEMuellaerss, mbr_approvedSet_OEMuellaerss);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_OEMuellaerss[1] = (a in mbr_approvedSet_OEMuellaerss);

% ---------------------------------------------------
%   PVS Änhänger
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_Aenhaenger = 1; 
int : mbr_k_Aenhaenger = 3; 
set of 1..mbr_k_Aenhaenger : mbr_approvedSet_Aenhaenger = {3}; 

% Decision variables: 
var bool: mbr_overall_Aenhaenger;
array[1..mbr_nScs_Aenhaenger] of var bool: mbr_valuations_Aenhaenger;
bool: mbr_top_Aenhaenger = true;
constraint mbr_overall_Aenhaenger = and (mbr_valuations_Aenhaenger,mbr_nScs_Aenhaenger, mbr_k_Aenhaenger, mbr_approvedSet_Aenhaenger);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_Aenhaenger[1] = (a in mbr_approvedSet_Aenhaenger);

% ---------------------------------------------------
%   PVS ÖÖüüÄÄ
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_OEOeueueAEAE = 1; 
int : mbr_k_OEOeueueAEAE = 3; 
set of 1..mbr_k_OEOeueueAEAE : mbr_approvedSet_OEOeueueAEAE = {2}; 

% Decision variables: 
var bool: mbr_overall_OEOeueueAEAE;
array[1..mbr_nScs_OEOeueueAEAE] of var bool: mbr_valuations_OEOeueueAEAE;
bool: mbr_top_OEOeueueAEAE = true;
constraint mbr_overall_OEOeueueAEAE = and (mbr_valuations_OEOeueueAEAE,mbr_nScs_OEOeueueAEAE, mbr_k_OEOeueueAEAE, mbr_approvedSet_OEOeueueAEAE);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_OEOeueueAEAE[1] = (a in mbr_approvedSet_OEOeueueAEAE);

% ---------------------------------------------------
%   PVS PRÄFIXÄhre
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_PRAEFIXAehre = 1; 
int : mbr_k_PRAEFIXAehre = 3; 
set of 1..mbr_k_PRAEFIXAehre : mbr_approvedSet_PRAEFIXAehre = {2}; 

% Decision variables: 
var bool: mbr_overall_PRAEFIXAehre;
array[1..mbr_nScs_PRAEFIXAehre] of var bool: mbr_valuations_PRAEFIXAehre;
bool: mbr_top_PRAEFIXAehre = true;
constraint mbr_overall_PRAEFIXAehre = and (mbr_valuations_PRAEFIXAehre,mbr_nScs_PRAEFIXAehre, mbr_k_PRAEFIXAehre, mbr_approvedSet_PRAEFIXAehre);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_PRAEFIXAehre[1] = (a in mbr_approvedSet_PRAEFIXAehre);
var 0..4: mbr_overall_MBR_VOT_1;
constraint mbr_overall_MBR_VOT_1 = 
(
bool2int(mbr_overall_OEMuellaerss)
+ bool2int(mbr_overall_Aenhaenger)
+ bool2int(mbr_overall_OEOeueueAEAE)
+ bool2int(mbr_overall_PRAEFIXAehre)
)
 ;
int: mbr_voter_count_MBR_VOT_1 = 4;
int: OEMuellaerss = 1;
int: Aenhaenger = 2;
int: OEOeueueAEAE = 3;
int: PRAEFIXAehre = 4;
array[1..mbr_voter_count_MBR_VOT_1] of string: mbr_voter_string_names_MBR_VOT_1 = ["ÖMüllärß", "Änhänger", "ÖÖüüÄÄ", "PRÄFIXÄhre"];

function ann: mbr_postGetBetter_MBR_VOT_1() = post(
(
bool2int(mbr_overall_OEMuellaerss)
+ bool2int(mbr_overall_Aenhaenger)
+ bool2int(mbr_overall_OEOeueueAEAE)
+ bool2int(mbr_overall_PRAEFIXAehre)
)
> 

(
bool2int(sol(mbr_overall_OEMuellaerss))
+ bool2int(sol(mbr_overall_Aenhaenger))
+ bool2int(sol(mbr_overall_OEOeueueAEAE))
+ bool2int(sol(mbr_overall_PRAEFIXAehre))
)
);

function ann: mbr_postNotGetWorse_MBR_VOT_1() = post(not ( (sol(mbr_overall_OEMuellaerss) = mbr_overall_OEMuellaerss /\ sol(mbr_overall_Aenhaenger) = mbr_overall_Aenhaenger /\ sol(mbr_overall_OEOeueueAEAE) = mbr_overall_OEOeueueAEAE /\ sol(mbr_overall_PRAEFIXAehre) = mbr_overall_PRAEFIXAehre) \/ (true)));

% ===============================================
% Generated code from MiniBrass, do not modify!
include "soft_constraints/mbr_types/weighted_type.mzn";
include "soft_constraints/mbr_types/cfn_type.mzn";
include "soft_constraints/mbr_types/cr_type.mzn";

% ---------------------------------------------------
% Overall exported predicate : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postBetter_overall();

% ---------------------------------------------------
%   PVS Orga
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_Orga = 3; 
int : mbr_k_Orga = 1000; 
bool : mbr_isWorstCase_Orga = true; 

% Decision variables: 
var 0..mbr_k_Orga: mbr_overall_Orga;
array[1..mbr_nScs_Orga] of var 0..mbr_k_Orga: mbr_valuations_Orga;
par 0..mbr_k_Orga: mbr_top_Orga = 0;

% MiniSearch predicates: 
constraint mbr_overall_Orga = sum (mbr_valuations_Orga,mbr_nScs_Orga, mbr_k_Orga, mbr_isWorstCase_Orga);

% Soft constraints: 
constraint mbr_valuations_Orga[1] = (violation[1]);
constraint mbr_valuations_Orga[2] = (violation[2]);
constraint mbr_valuations_Orga[3] = (violation[3]);

% ---------------------------------------------------
%   PVS biogas
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_biogas = 3; 
array[int, 1..2] of 1..mbr_nScs_biogas : mbr_crEdges_biogas = [| 2, 1 | 3, 1 |]; 
bool : mbr_useSPD_biogas = true; 

% Decision variables: 
var set of 1..mbr_nScs_biogas: mbr_overall_biogas;
array[1..mbr_nScs_biogas] of var bool: mbr_valuations_biogas;
par set of 1..mbr_nScs_biogas: mbr_top_biogas = {};

% MiniSearch predicates: 
constraint mbr_overall_biogas = link_invert_booleans (mbr_valuations_biogas,mbr_nScs_biogas, mbr_crEdges_biogas, mbr_useSPD_biogas);

% Soft constraints: 
constraint mbr_valuations_biogas[1] = (forall(w in WINDOW) (supply[w,biogas] >= 13));
constraint mbr_valuations_biogas[2] = (forall(w in WINDOW) (supply[w,biogas] >= 14));
constraint mbr_valuations_biogas[3] = (forall(w in 1..T-1) ( abs(supply[w,biogas] - supply[w+1,biogas]) <= 1));

% ---------------------------------------------------
%   PVS ev
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ev = 3; 
array[int, 1..2] of 1..mbr_nScs_ev : mbr_crEdges_ev = [| 3, 1 |  3, 2 |]; 
bool : mbr_useSPD_ev = false; 

% Decision variables: 
var set of 1..mbr_nScs_ev: mbr_overall_ev;
array[1..mbr_nScs_ev] of var bool: mbr_valuations_ev;
par set of 1..mbr_nScs_ev: mbr_top_ev = {};

% MiniSearch predicates: 
constraint mbr_overall_ev = link_invert_booleans (mbr_valuations_ev,mbr_nScs_ev, mbr_crEdges_ev, mbr_useSPD_ev);

% Soft constraints: 
constraint mbr_valuations_ev[1] = (forall(w in WINDOW) (supply[w,EV] in 5..8));
constraint mbr_valuations_ev[2] = (supply[2,EV] = 5 /\ supply[3,EV] = 5);
constraint mbr_valuations_ev[3] = (sum(w in WINDOW) ( bool2int(supply[w,EV] == 11) ) <= 2);

% ---------------------------------------------------
%   PVS therm1
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_therm1 = 2; 
int : mbr_k_therm1 = 1000; 
bool : mbr_isWorstCase_therm1 = false; 

% Decision variables: 
var 0..mbr_k_therm1: mbr_overall_therm1;
array[1..mbr_nScs_therm1] of var 0..mbr_k_therm1: mbr_valuations_therm1;
par 0..mbr_k_therm1: mbr_top_therm1 = 0;

% MiniSearch predicates: 
constraint mbr_overall_therm1 = sum (mbr_valuations_therm1,mbr_nScs_therm1, mbr_k_therm1, mbr_isWorstCase_therm1);

% Soft constraints: 
constraint mbr_valuations_therm1[1] = (sum(w in WINDOW) ( abs(supply[w,thermal] - 8) ));
constraint mbr_valuations_therm1[2] = (sum(w in 1..T-1) ( abs(supply[w,thermal] - supply[w+1,thermal])));

% ---------------------------------------------------
%   PVS therm2
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_therm2 = 1; 
int : mbr_k_therm2 = 1000; 
bool : mbr_isWorstCase_therm2 = false; 

% Decision variables: 
var 0..mbr_k_therm2: mbr_overall_therm2;
array[1..mbr_nScs_therm2] of var 0..mbr_k_therm2: mbr_valuations_therm2;
par 0..mbr_k_therm2: mbr_top_therm2 = 0;

% MiniSearch predicates: 
constraint mbr_overall_therm2 = sum (mbr_valuations_therm2,mbr_nScs_therm2, mbr_k_therm2, mbr_isWorstCase_therm2);

% Soft constraints: 
constraint mbr_valuations_therm2[1] = (sum(w in WINDOW) ( abs(supply[w,3] - 9) ));

function ann: mbr_postBetter_overall() = post(( (is_worse_weighted(sol(mbr_overall_Orga), mbr_overall_Orga, mbr_nScs_Orga, mbr_k_Orga, mbr_isWorstCase_Orga)) \/ (sol(mbr_overall_Orga) = mbr_overall_Orga /\ ( (( (is_worse_cr(sol(mbr_overall_biogas), mbr_overall_biogas, mbr_nScs_biogas, mbr_crEdges_biogas, mbr_useSPD_biogas)) /\ (is_worse_cr(sol(mbr_overall_ev), mbr_overall_ev, mbr_nScs_ev, mbr_crEdges_ev, mbr_useSPD_ev)) )) /\ (( (is_worse_weighted(sol(mbr_overall_therm1), mbr_overall_therm1, mbr_nScs_therm1, mbr_k_therm1, mbr_isWorstCase_therm1)) \/ (sol(mbr_overall_therm1) = mbr_overall_therm1 /\ is_worse_weighted(sol(mbr_overall_therm2), mbr_overall_therm2, mbr_nScs_therm2, mbr_k_therm2, mbr_isWorstCase_therm2)) )) )) ));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_Orga = \(mbr_overall_Orga); mbr_overall_biogas = \(mbr_overall_biogas); mbr_overall_ev = \(mbr_overall_ev); mbr_overall_therm1 = \(mbr_overall_therm1); mbr_overall_therm2 = \(mbr_overall_therm2)\n"]

% ===============================================
% Generated code from MiniBrass, do not modify!
include "soft_constraints/mbr_types/cr_type.mzn";
include "soft_constraints/mbr_types/probabilistic_type.mzn";
include "soft_constraints/mbr_types/fuzzy_type.mzn";
include "soft_constraints/mbr_types/weighted_type.mzn";
include "soft_constraints/mbr_types/free-pvs-type.mzn";
include "soft_constraints/mbr_types/cfn_type.mzn";

% ---------------------------------------------------
% Overall exported predicate (and objective in case of atomic top-level PVS) : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postGetBetter_foodPvs();
function ann:  postNotGetWorse() = mbr_postNotGetWorse_foodPvs();
var set of 1..mbr_nScs_foodPvs: topLevelObjective;
constraint topLevelObjective = mbr_overall_foodPvs;

% ---------------------------------------------------
%   PVS foodPvs
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_foodPvs = 3; 
array[int, 1..2] of 1..mbr_nScs_foodPvs : mbr_crEdges_foodPvs = [|1, 2 | 3, 2|]; 
bool : mbr_useSPD_foodPvs = true; 

% Decision variables: 
var set of 1..mbr_nScs_foodPvs: mbr_overall_foodPvs;
array[1..mbr_nScs_foodPvs] of var bool: mbr_valuations_foodPvs;

% MiniSearch predicates: 
constraint mbr_overall_foodPvs = link_invert_booleans (mbr_valuations_foodPvs,mbr_nScs_foodPvs, mbr_crEdges_foodPvs, mbr_useSPD_foodPvs);

% Soft constraints: 
constraint mbr_valuations_foodPvs[1] = (dinnerSelected[burger]);
constraint mbr_valuations_foodPvs[2] = (dinnerSelected[grillen]);
constraint mbr_valuations_foodPvs[3] = (dinnerSelected[schweinebraten]);

function ann: mbr_postGetBetter_foodPvs() = post(is_worse_cr(sol(mbr_overall_foodPvs), mbr_overall_foodPvs, mbr_nScs_foodPvs, mbr_crEdges_foodPvs, mbr_useSPD_foodPvs));

function ann: mbr_postNotGetWorse_foodPvs() = post(not ( (sol(mbr_overall_foodPvs) = mbr_overall_foodPvs) \/ (is_worse_cr(mbr_overall_foodPvs, sol(mbr_overall_foodPvs), mbr_nScs_foodPvs, mbr_crEdges_foodPvs, mbr_useSPD_foodPvs))));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_foodPvs = \(mbr_overall_foodPvs)\n"]

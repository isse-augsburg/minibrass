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
function ann:  postGetBetter() = mbr_postGetBetter_beers();
function ann:  postNotGetWorse() = mbr_postNotGetWorse_beers();
var 0..mbr_k_beers: topLevelObjective;
constraint topLevelObjective = mbr_overall_beers;

% ---------------------------------------------------
%   PVS beers
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_beers = 1; 
int : mbr_k_beers = 1000; 

% Decision variables: 
var 0..mbr_k_beers: mbr_overall_beers;
array[1..mbr_nScs_beers] of var 0..mbr_k_beers: mbr_valuations_beers;

% MiniSearch predicates: 
constraint mbr_overall_beers = sum (mbr_valuations_beers,mbr_nScs_beers, mbr_k_beers);

% Soft constraints: 
constraint mbr_valuations_beers[1] = (pow(2, abs(4 - (monbeer+tuesbeer))) );

function ann: mbr_postGetBetter_beers() = post(is_worse_weighted(sol(mbr_overall_beers), mbr_overall_beers, mbr_nScs_beers, mbr_k_beers));

function ann: mbr_postNotGetWorse_beers() = post(not ( (sol(mbr_overall_beers) = mbr_overall_beers) \/ (is_worse_weighted(mbr_overall_beers, sol(mbr_overall_beers), mbr_nScs_beers, mbr_k_beers))));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_beers = \(mbr_overall_beers)\n"]

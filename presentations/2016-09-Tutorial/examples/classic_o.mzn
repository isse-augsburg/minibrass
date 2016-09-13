% ===============================================
% Generated code from MiniBrass, do not modify!
include "../mbr_types/cr_type.mzn";

% ---------------------------------------------------
% Overall exported predicate : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postBetter_cr1();

% ---------------------------------------------------
%   PVS cr1
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_cr1 = 3; 
array[int, 1..2] of 1..mbr_nScs_cr1 : mbr_crEdges_cr1 = [| 2, 1 | 3, 1 |]; 
bool : mbr_useSPD_cr1 = false; 

% Decision variables: 
var set of 1..mbr_nScs_cr1: mbr_overall_cr1;
array[1..mbr_nScs_cr1] of var bool: mbr_valuations_cr1;
par set of 1..mbr_nScs_cr1: mbr_top_cr1 = {};

% MiniSearch predicates: 
constraint mbr_overall_cr1 = link_invert_booleans (mbr_valuations_cr1,mbr_nScs_cr1, mbr_crEdges_cr1, mbr_useSPD_cr1);

% Soft constraints: 
constraint mbr_valuations_cr1[1] = (x + 1 = y);
constraint mbr_valuations_cr1[2] = (z = y + 2);
constraint mbr_valuations_cr1[3] = (x + y <= 3);

function ann: mbr_postBetter_cr1() = post(is_worse_cr(sol(mbr_overall_cr1), mbr_overall_cr1, mbr_nScs_cr1, mbr_crEdges_cr1, mbr_useSPD_cr1));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_cr1 = \(mbr_overall_cr1)\n"]

% ===============================================
% Generated code from MiniBrass, do not modify!
include "../mbr_types/weighted_type.mzn";
include "../mbr_types/cr_type.mzn";

% ---------------------------------------------------
% Overall exported predicate : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postBetter_ToWeighted_RefTo_cr1_();

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_cr1_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_cr1_ = 3; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_cr1_ : mbr_crEdges_ToWeighted_RefTo_cr1_ = [| 2, 1 | 3, 1 |]; 
bool : mbr_useSPD_ToWeighted_RefTo_cr1_ = false; 
int : mbr_k_ToWeighted_RefTo_cr1_ = mbr_nScs_ToWeighted_RefTo_cr1_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_cr1_) (mbr_weights_ToWeighted_RefTo_cr1_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_cr1_] of int : mbr_weights_ToWeighted_RefTo_cr1_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_cr1_, mbr_crEdges_ToWeighted_RefTo_cr1_, mbr_useSPD_ToWeighted_RefTo_cr1_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_cr1_;
array[1..mbr_nScs_ToWeighted_RefTo_cr1_] of var bool: mbr_valuations_ToWeighted_RefTo_cr1_;
par int: mbr_top_ToWeighted_RefTo_cr1_ = 0;

% MiniSearch predicates: 
constraint mbr_overall_ToWeighted_RefTo_cr1_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_cr1_,mbr_nScs_ToWeighted_RefTo_cr1_, mbr_k_ToWeighted_RefTo_cr1_, mbr_weights_ToWeighted_RefTo_cr1_);

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_cr1_[1] = (x + 1 = y);
constraint mbr_valuations_ToWeighted_RefTo_cr1_[2] = (z = y + 2);
constraint mbr_valuations_ToWeighted_RefTo_cr1_[3] = (x + y <= 3);

function ann: mbr_postBetter_ToWeighted_RefTo_cr1_() = post(is_worse_weighted(sol(mbr_overall_ToWeighted_RefTo_cr1_), mbr_overall_ToWeighted_RefTo_cr1_, mbr_nScs_ToWeighted_RefTo_cr1_, mbr_k_ToWeighted_RefTo_cr1_, mbr_weights_ToWeighted_RefTo_cr1_));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_ToWeighted_RefTo_cr1_ = \(mbr_overall_ToWeighted_RefTo_cr1_)\n"]

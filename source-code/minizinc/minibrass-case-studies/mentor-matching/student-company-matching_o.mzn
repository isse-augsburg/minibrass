% ===============================================
% Generated code from MiniBrass, do not modify!
include "soft_constraints/mbr_types/weighted_type.mzn";
include "soft_constraints/mbr_types/cfn_type.mzn";
include "soft_constraints/mbr_types/cr_type.mzn";

% ---------------------------------------------------
% Overall exported predicate : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postBetter_MBR_LEX_1();

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_companies_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_companies_ = 3; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_companies_ : mbr_crEdges_ToWeighted_RefTo_companies_ = [| 1, 2 |]; 
bool : mbr_useSPD_ToWeighted_RefTo_companies_ = true; 
int : mbr_k_ToWeighted_RefTo_companies_ = mbr_nScs_ToWeighted_RefTo_companies_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_companies_) (mbr_weights_ToWeighted_RefTo_companies_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_companies_] of int : mbr_weights_ToWeighted_RefTo_companies_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_companies_, mbr_crEdges_ToWeighted_RefTo_companies_, mbr_useSPD_ToWeighted_RefTo_companies_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_companies_;
array[1..mbr_nScs_ToWeighted_RefTo_companies_] of var bool: mbr_valuations_ToWeighted_RefTo_companies_;
par int: mbr_top_ToWeighted_RefTo_companies_ = 0;

% MiniSearch predicates: 
constraint mbr_overall_ToWeighted_RefTo_companies_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_companies_,mbr_nScs_ToWeighted_RefTo_companies_, mbr_k_ToWeighted_RefTo_companies_, mbr_weights_ToWeighted_RefTo_companies_);

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_companies_[1] = (worksAt[meerfluss] = delphi);
constraint mbr_valuations_ToWeighted_RefTo_companies_[2] = (worksAt[gleich] = delphi);
constraint mbr_valuations_ToWeighted_RefTo_companies_[3] = (worksAt[lustig] = youthlab);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_students_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_students_ = 3; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_students_ : mbr_crEdges_ToWeighted_RefTo_students_ = [| 2, 1 | 3, 1 |]; 
bool : mbr_useSPD_ToWeighted_RefTo_students_ = true; 
int : mbr_k_ToWeighted_RefTo_students_ = mbr_nScs_ToWeighted_RefTo_students_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_students_) (mbr_weights_ToWeighted_RefTo_students_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_students_] of int : mbr_weights_ToWeighted_RefTo_students_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_students_, mbr_crEdges_ToWeighted_RefTo_students_, mbr_useSPD_ToWeighted_RefTo_students_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_students_;
array[1..mbr_nScs_ToWeighted_RefTo_students_] of var bool: mbr_valuations_ToWeighted_RefTo_students_;
par int: mbr_top_ToWeighted_RefTo_students_ = 0;

% MiniSearch predicates: 
constraint mbr_overall_ToWeighted_RefTo_students_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_students_,mbr_nScs_ToWeighted_RefTo_students_, mbr_k_ToWeighted_RefTo_students_, mbr_weights_ToWeighted_RefTo_students_);

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_students_[1] = (worksAt[raubholz] = delphi);
constraint mbr_valuations_ToWeighted_RefTo_students_[2] = (worksAt[raubholz] = youthlab);
constraint mbr_valuations_ToWeighted_RefTo_students_[3] = (worksAt[gleich] = cupgainini);

function ann: mbr_postBetter_MBR_LEX_1() = post(( (is_worse_weighted(sol(mbr_overall_ToWeighted_RefTo_companies_), mbr_overall_ToWeighted_RefTo_companies_, mbr_nScs_ToWeighted_RefTo_companies_, mbr_k_ToWeighted_RefTo_companies_, mbr_weights_ToWeighted_RefTo_companies_)) \/ (sol(mbr_overall_ToWeighted_RefTo_companies_) = mbr_overall_ToWeighted_RefTo_companies_ /\ is_worse_weighted(sol(mbr_overall_ToWeighted_RefTo_students_), mbr_overall_ToWeighted_RefTo_students_, mbr_nScs_ToWeighted_RefTo_students_, mbr_k_ToWeighted_RefTo_students_, mbr_weights_ToWeighted_RefTo_students_)) ));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_ToWeighted_RefTo_companies_ = \(mbr_overall_ToWeighted_RefTo_companies_); mbr_overall_ToWeighted_RefTo_students_ = \(mbr_overall_ToWeighted_RefTo_students_)\n"]

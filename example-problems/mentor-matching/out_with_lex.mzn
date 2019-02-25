% ===============================================
% Generated code from MiniBrass, do not modify!
include "soft_constraints/mbr_types/probabilistic_type.mzn";
include "soft_constraints/mbr_types/fuzzy_type.mzn";
include "soft_constraints/mbr_types/relation-type.mzn";
include "soft_constraints/mbr_types/bool-type.mzn";
include "soft_constraints/mbr_types/cfn_type.mzn";
include "soft_constraints/mbr_types/weighted_type.mzn";
include "soft_constraints/mbr_types/cr_type.mzn";
include "soft_constraints/mbr_types/free-pvs-type.mzn";

% ---------------------------------------------------
% Overall exported predicate (and objective in case of atomic top-level PVS) : 

% ---------------------------------------------------
function ann:  postGetBetter() = mbr_postGetBetter_MBR_LEX_2();
function ann:  postNotGetWorse() = mbr_postNotGetWorse_MBR_LEX_2();

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_anneser_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_anneser_ = 5; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_anneser_ : mbr_crEdges_ToWeighted_RefTo_anneser_ = [|2, 1 | 3, 2 | 4, 3 | 5, 4 | 3, 1 | 4, 1 | 4, 2 | 5, 1 | 5, 2 | 5, 3|]; 
bool : mbr_useSPD_ToWeighted_RefTo_anneser_ = true; 
int : mbr_k_ToWeighted_RefTo_anneser_ = mbr_nScs_ToWeighted_RefTo_anneser_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_anneser_) (mbr_weights_ToWeighted_RefTo_anneser_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_anneser_] of int : mbr_weights_ToWeighted_RefTo_anneser_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_anneser_, mbr_crEdges_ToWeighted_RefTo_anneser_, mbr_useSPD_ToWeighted_RefTo_anneser_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_anneser_;
array[1..mbr_nScs_ToWeighted_RefTo_anneser_] of var bool: mbr_valuations_ToWeighted_RefTo_anneser_;
int: mbr_top_ToWeighted_RefTo_anneser_ = 0;
constraint mbr_overall_ToWeighted_RefTo_anneser_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_anneser_,mbr_nScs_ToWeighted_RefTo_anneser_, mbr_k_ToWeighted_RefTo_anneser_, mbr_weights_ToWeighted_RefTo_anneser_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_anneser_[1] = (worksAt[anneser] = msg);
constraint mbr_valuations_ToWeighted_RefTo_anneser_[2] = (worksAt[anneser] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_anneser_[3] = (worksAt[anneser] = zue);
constraint mbr_valuations_ToWeighted_RefTo_anneser_[4] = (worksAt[anneser] = ora);
constraint mbr_valuations_ToWeighted_RefTo_anneser_[5] = (worksAt[anneser] = cap);

% ---------------------------------------------------
%   PVS brandt
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_brandt = 10; 
int : mbr_k_brandt = 1000; 
array[1..mbr_nScs_brandt] of int : mbr_weights_brandt = [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]; 

% Decision variables: 
var int: mbr_overall_brandt;
array[1..mbr_nScs_brandt] of var bool: mbr_valuations_brandt;
int: mbr_top_brandt = 0;
constraint mbr_overall_brandt = weighted_sum (mbr_valuations_brandt,mbr_nScs_brandt, mbr_k_brandt, mbr_weights_brandt);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_brandt[1] = (worksAt[brandt] = mai);
constraint mbr_valuations_brandt[2] = (worksAt[brandt] = sie);
constraint mbr_valuations_brandt[3] = (worksAt[brandt] = msg);
constraint mbr_valuations_brandt[4] = (worksAt[brandt] = zue);
constraint mbr_valuations_brandt[5] = (worksAt[brandt] = kuk);
constraint mbr_valuations_brandt[6] = (worksAt[brandt] = ora);
constraint mbr_valuations_brandt[7] = (worksAt[brandt] = sap);
constraint mbr_valuations_brandt[8] = (worksAt[brandt] = cap);
constraint mbr_valuations_brandt[9] = (worksAt[brandt] = bmw);
constraint mbr_valuations_brandt[10] = (worksAt[brandt] = qpl);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_martin_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_martin_ = 4; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_martin_ : mbr_crEdges_ToWeighted_RefTo_martin_ = [|2, 1 | 3, 2 | 4, 3 | 3, 1 | 4, 1 | 4, 2|]; 
bool : mbr_useSPD_ToWeighted_RefTo_martin_ = false; 
int : mbr_k_ToWeighted_RefTo_martin_ = mbr_nScs_ToWeighted_RefTo_martin_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_martin_) (mbr_weights_ToWeighted_RefTo_martin_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_martin_] of int : mbr_weights_ToWeighted_RefTo_martin_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_martin_, mbr_crEdges_ToWeighted_RefTo_martin_, mbr_useSPD_ToWeighted_RefTo_martin_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_martin_;
array[1..mbr_nScs_ToWeighted_RefTo_martin_] of var bool: mbr_valuations_ToWeighted_RefTo_martin_;
int: mbr_top_ToWeighted_RefTo_martin_ = 0;
constraint mbr_overall_ToWeighted_RefTo_martin_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_martin_,mbr_nScs_ToWeighted_RefTo_martin_, mbr_k_ToWeighted_RefTo_martin_, mbr_weights_ToWeighted_RefTo_martin_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_martin_[1] = (worksAt[martin] in { mai, ora });
constraint mbr_valuations_ToWeighted_RefTo_martin_[2] = (worksAt[martin] in { zue, qpl });
constraint mbr_valuations_ToWeighted_RefTo_martin_[3] = (worksAt[martin] in { kuk, sie });
constraint mbr_valuations_ToWeighted_RefTo_martin_[4] = (worksAt[martin] in { bmw, cap, msg, sap });

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_alhomssi_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_alhomssi_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_alhomssi_ : mbr_crEdges_ToWeighted_RefTo_alhomssi_ = [|2, 1 | 3, 2 | 4, 3 | 5, 4 | 6, 5 | 7, 6 | 8, 9 | 9, 7 | 10, 8 | 3, 1 | 4, 1 | 4, 2 | 5, 1 | 5, 2 | 5, 3 | 6, 1 | 6, 2 | 6, 3 | 6, 4 | 7, 1 | 7, 2 | 7, 3 | 7, 4 | 7, 5 | 8, 7 | 9, 1 | 9, 2 | 9, 3 | 9, 4 | 9, 5 | 9, 6 | 10, 7 | 10, 9 | 8, 1 | 8, 2 | 8, 3 | 8, 4 | 8, 5 | 8, 6 | 10, 1 | 10, 2 | 10, 3 | 10, 4 | 10, 5 | 10, 6|]; 
bool : mbr_useSPD_ToWeighted_RefTo_alhomssi_ = false; 
int : mbr_k_ToWeighted_RefTo_alhomssi_ = mbr_nScs_ToWeighted_RefTo_alhomssi_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_alhomssi_) (mbr_weights_ToWeighted_RefTo_alhomssi_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_alhomssi_] of int : mbr_weights_ToWeighted_RefTo_alhomssi_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_alhomssi_, mbr_crEdges_ToWeighted_RefTo_alhomssi_, mbr_useSPD_ToWeighted_RefTo_alhomssi_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_alhomssi_;
array[1..mbr_nScs_ToWeighted_RefTo_alhomssi_] of var bool: mbr_valuations_ToWeighted_RefTo_alhomssi_;
int: mbr_top_ToWeighted_RefTo_alhomssi_ = 0;
constraint mbr_overall_ToWeighted_RefTo_alhomssi_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_alhomssi_,mbr_nScs_ToWeighted_RefTo_alhomssi_, mbr_k_ToWeighted_RefTo_alhomssi_, mbr_weights_ToWeighted_RefTo_alhomssi_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[1] = (worksAt[alhomssi] = ora);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[2] = (worksAt[alhomssi] = msg);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[3] = (worksAt[alhomssi] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[4] = (worksAt[alhomssi] = zue);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[5] = (worksAt[alhomssi] = sie);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[6] = (worksAt[alhomssi] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[7] = (worksAt[alhomssi] = sap);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[8] = (worksAt[alhomssi] = mai);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[9] = (worksAt[alhomssi] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_alhomssi_[10] = (worksAt[alhomssi] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_beeh_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_beeh_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_beeh_ : mbr_crEdges_ToWeighted_RefTo_beeh_ = [|1, 10 | 3, 1 | 4, 5 | 5, 7 | 6, 3 | 7, 2 | 8, 6 | 9, 8 | 10, 4 | 1, 4 | 3, 4 | 3, 10 | 4, 7 | 5, 2 | 6, 1 | 6, 4 | 6, 10 | 8, 1 | 8, 3 | 8, 4 | 8, 10 | 9, 1 | 9, 3 | 9, 4 | 9, 6 | 9, 10 | 10, 5 | 10, 7 | 1, 5 | 1, 7 | 3, 5 | 3, 7 | 4, 2 | 6, 2 | 6, 5 | 6, 7 | 8, 2 | 8, 5 | 8, 7 | 9, 2 | 9, 5 | 9, 7 | 10, 2 | 1, 2 | 3, 2|]; 
bool : mbr_useSPD_ToWeighted_RefTo_beeh_ = false; 
int : mbr_k_ToWeighted_RefTo_beeh_ = mbr_nScs_ToWeighted_RefTo_beeh_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_beeh_) (mbr_weights_ToWeighted_RefTo_beeh_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_beeh_] of int : mbr_weights_ToWeighted_RefTo_beeh_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_beeh_, mbr_crEdges_ToWeighted_RefTo_beeh_, mbr_useSPD_ToWeighted_RefTo_beeh_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_beeh_;
array[1..mbr_nScs_ToWeighted_RefTo_beeh_] of var bool: mbr_valuations_ToWeighted_RefTo_beeh_;
int: mbr_top_ToWeighted_RefTo_beeh_ = 0;
constraint mbr_overall_ToWeighted_RefTo_beeh_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_beeh_,mbr_nScs_ToWeighted_RefTo_beeh_, mbr_k_ToWeighted_RefTo_beeh_, mbr_weights_ToWeighted_RefTo_beeh_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_beeh_[1] = (worksAt[beeh] = ora);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[2] = (worksAt[beeh] = msg);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[3] = (worksAt[beeh] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[4] = (worksAt[beeh] = zue);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[5] = (worksAt[beeh] = sie);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[6] = (worksAt[beeh] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[7] = (worksAt[beeh] = mai);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[8] = (worksAt[beeh] = sap);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[9] = (worksAt[beeh] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_beeh_[10] = (worksAt[beeh] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_schroll_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_schroll_ = 5; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_schroll_ : mbr_crEdges_ToWeighted_RefTo_schroll_ = [|1, 3 | 2, 5 | 3, 4 | 4, 2 | 1, 4 | 3, 2 | 4, 5 | 1, 2 | 1, 5 | 3, 5|]; 
bool : mbr_useSPD_ToWeighted_RefTo_schroll_ = true; 
int : mbr_k_ToWeighted_RefTo_schroll_ = mbr_nScs_ToWeighted_RefTo_schroll_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_schroll_) (mbr_weights_ToWeighted_RefTo_schroll_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_schroll_] of int : mbr_weights_ToWeighted_RefTo_schroll_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_schroll_, mbr_crEdges_ToWeighted_RefTo_schroll_, mbr_useSPD_ToWeighted_RefTo_schroll_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_schroll_;
array[1..mbr_nScs_ToWeighted_RefTo_schroll_] of var bool: mbr_valuations_ToWeighted_RefTo_schroll_;
int: mbr_top_ToWeighted_RefTo_schroll_ = 0;
constraint mbr_overall_ToWeighted_RefTo_schroll_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_schroll_,mbr_nScs_ToWeighted_RefTo_schroll_, mbr_k_ToWeighted_RefTo_schroll_, mbr_weights_ToWeighted_RefTo_schroll_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_schroll_[1] = (worksAt[schroll] = msg);
constraint mbr_valuations_ToWeighted_RefTo_schroll_[2] = (worksAt[schroll] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_schroll_[3] = (worksAt[schroll] = zue);
constraint mbr_valuations_ToWeighted_RefTo_schroll_[4] = (worksAt[schroll] = sie);
constraint mbr_valuations_ToWeighted_RefTo_schroll_[5] = (worksAt[schroll] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_keppner_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_keppner_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_keppner_ : mbr_crEdges_ToWeighted_RefTo_keppner_ = [|1, 4 | 2, 10 | 3, 5 | 4, 2 | 5, 9 | 6, 1 | 8, 6 | 9, 8 | 10, 7 | 1, 2 | 2, 7 | 3, 9 | 4, 7 | 4, 10 | 5, 8 | 6, 2 | 6, 4 | 8, 1 | 8, 2 | 8, 4 | 9, 1 | 9, 2 | 9, 4 | 9, 6 | 1, 7 | 1, 10 | 3, 1 | 3, 2 | 3, 4 | 3, 6 | 3, 8 | 5, 1 | 5, 2 | 5, 4 | 5, 6 | 6, 7 | 6, 10 | 8, 7 | 8, 10 | 9, 7 | 9, 10 | 3, 7 | 3, 10 | 5, 7 | 5, 10|]; 
bool : mbr_useSPD_ToWeighted_RefTo_keppner_ = true; 
int : mbr_k_ToWeighted_RefTo_keppner_ = mbr_nScs_ToWeighted_RefTo_keppner_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_keppner_) (mbr_weights_ToWeighted_RefTo_keppner_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_keppner_] of int : mbr_weights_ToWeighted_RefTo_keppner_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_keppner_, mbr_crEdges_ToWeighted_RefTo_keppner_, mbr_useSPD_ToWeighted_RefTo_keppner_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_keppner_;
array[1..mbr_nScs_ToWeighted_RefTo_keppner_] of var bool: mbr_valuations_ToWeighted_RefTo_keppner_;
int: mbr_top_ToWeighted_RefTo_keppner_ = 0;
constraint mbr_overall_ToWeighted_RefTo_keppner_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_keppner_,mbr_nScs_ToWeighted_RefTo_keppner_, mbr_k_ToWeighted_RefTo_keppner_, mbr_weights_ToWeighted_RefTo_keppner_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_keppner_[1] = (worksAt[keppner] = ora);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[2] = (worksAt[keppner] = msg);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[3] = (worksAt[keppner] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[4] = (worksAt[keppner] = zue);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[5] = (worksAt[keppner] = sie);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[6] = (worksAt[keppner] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[7] = (worksAt[keppner] = mai);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[8] = (worksAt[keppner] = sap);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[9] = (worksAt[keppner] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_keppner_[10] = (worksAt[keppner] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_henrich_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_henrich_ = 3; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_henrich_ : mbr_crEdges_ToWeighted_RefTo_henrich_ = [|2, 1 | 3, 2 | 3, 1|]; 
bool : mbr_useSPD_ToWeighted_RefTo_henrich_ = false; 
int : mbr_k_ToWeighted_RefTo_henrich_ = mbr_nScs_ToWeighted_RefTo_henrich_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_henrich_) (mbr_weights_ToWeighted_RefTo_henrich_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_henrich_] of int : mbr_weights_ToWeighted_RefTo_henrich_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_henrich_, mbr_crEdges_ToWeighted_RefTo_henrich_, mbr_useSPD_ToWeighted_RefTo_henrich_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_henrich_;
array[1..mbr_nScs_ToWeighted_RefTo_henrich_] of var bool: mbr_valuations_ToWeighted_RefTo_henrich_;
int: mbr_top_ToWeighted_RefTo_henrich_ = 0;
constraint mbr_overall_ToWeighted_RefTo_henrich_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_henrich_,mbr_nScs_ToWeighted_RefTo_henrich_, mbr_k_ToWeighted_RefTo_henrich_, mbr_weights_ToWeighted_RefTo_henrich_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_henrich_[1] = (worksAt[henrich] in { sie, mai });
constraint mbr_valuations_ToWeighted_RefTo_henrich_[2] = (worksAt[henrich] in { zue, msg });
constraint mbr_valuations_ToWeighted_RefTo_henrich_[3] = (worksAt[henrich] in { bmw, qpl, cap });

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_pichlmair_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_pichlmair_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_pichlmair_ : mbr_crEdges_ToWeighted_RefTo_pichlmair_ = [|1, 6 | 2, 10 | 3, 5 | 4, 9 | 6, 4 | 7, 3 | 8, 1 | 9, 7 | 10, 8 | 1, 4 | 2, 8 | 4, 7 | 6, 7 | 6, 9 | 7, 5 | 8, 4 | 8, 6 | 9, 3 | 9, 5 | 10, 1 | 10, 4 | 10, 6 | 1, 7 | 1, 9 | 2, 1 | 2, 4 | 2, 6 | 4, 3 | 4, 5 | 6, 3 | 6, 5 | 8, 3 | 8, 5 | 8, 7 | 8, 9 | 10, 3 | 10, 5 | 10, 7 | 10, 9 | 1, 3 | 1, 5 | 2, 3 | 2, 5 | 2, 7 | 2, 9|]; 
bool : mbr_useSPD_ToWeighted_RefTo_pichlmair_ = true; 
int : mbr_k_ToWeighted_RefTo_pichlmair_ = mbr_nScs_ToWeighted_RefTo_pichlmair_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_pichlmair_) (mbr_weights_ToWeighted_RefTo_pichlmair_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_pichlmair_] of int : mbr_weights_ToWeighted_RefTo_pichlmair_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_pichlmair_, mbr_crEdges_ToWeighted_RefTo_pichlmair_, mbr_useSPD_ToWeighted_RefTo_pichlmair_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_pichlmair_;
array[1..mbr_nScs_ToWeighted_RefTo_pichlmair_] of var bool: mbr_valuations_ToWeighted_RefTo_pichlmair_;
int: mbr_top_ToWeighted_RefTo_pichlmair_ = 0;
constraint mbr_overall_ToWeighted_RefTo_pichlmair_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_pichlmair_,mbr_nScs_ToWeighted_RefTo_pichlmair_, mbr_k_ToWeighted_RefTo_pichlmair_, mbr_weights_ToWeighted_RefTo_pichlmair_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[1] = (worksAt[pichlmair] = ora);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[2] = (worksAt[pichlmair] = msg);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[3] = (worksAt[pichlmair] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[4] = (worksAt[pichlmair] = zue);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[5] = (worksAt[pichlmair] = sie);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[6] = (worksAt[pichlmair] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[7] = (worksAt[pichlmair] = mai);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[8] = (worksAt[pichlmair] = sap);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[9] = (worksAt[pichlmair] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_pichlmair_[10] = (worksAt[pichlmair] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_schwarzmeier_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_schwarzmeier_ = 3; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_schwarzmeier_ : mbr_crEdges_ToWeighted_RefTo_schwarzmeier_ = [|1, 2 | 3, 1 | 3, 2|]; 
bool : mbr_useSPD_ToWeighted_RefTo_schwarzmeier_ = true; 
int : mbr_k_ToWeighted_RefTo_schwarzmeier_ = mbr_nScs_ToWeighted_RefTo_schwarzmeier_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_schwarzmeier_) (mbr_weights_ToWeighted_RefTo_schwarzmeier_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_schwarzmeier_] of int : mbr_weights_ToWeighted_RefTo_schwarzmeier_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_schwarzmeier_, mbr_crEdges_ToWeighted_RefTo_schwarzmeier_, mbr_useSPD_ToWeighted_RefTo_schwarzmeier_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_schwarzmeier_;
array[1..mbr_nScs_ToWeighted_RefTo_schwarzmeier_] of var bool: mbr_valuations_ToWeighted_RefTo_schwarzmeier_;
int: mbr_top_ToWeighted_RefTo_schwarzmeier_ = 0;
constraint mbr_overall_ToWeighted_RefTo_schwarzmeier_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_schwarzmeier_,mbr_nScs_ToWeighted_RefTo_schwarzmeier_, mbr_k_ToWeighted_RefTo_schwarzmeier_, mbr_weights_ToWeighted_RefTo_schwarzmeier_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_schwarzmeier_[1] = (worksAt[schwarzmeier] = ora);
constraint mbr_valuations_ToWeighted_RefTo_schwarzmeier_[2] = (worksAt[schwarzmeier] = sie);
constraint mbr_valuations_ToWeighted_RefTo_schwarzmeier_[3] = (worksAt[schwarzmeier] = qpl);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_biederer_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_biederer_ = 4; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_biederer_ : mbr_crEdges_ToWeighted_RefTo_biederer_ = [|2, 1 | 3, 2 | 4, 3 | 3, 1 | 4, 1 | 4, 2|]; 
bool : mbr_useSPD_ToWeighted_RefTo_biederer_ = true; 
int : mbr_k_ToWeighted_RefTo_biederer_ = mbr_nScs_ToWeighted_RefTo_biederer_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_biederer_) (mbr_weights_ToWeighted_RefTo_biederer_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_biederer_] of int : mbr_weights_ToWeighted_RefTo_biederer_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_biederer_, mbr_crEdges_ToWeighted_RefTo_biederer_, mbr_useSPD_ToWeighted_RefTo_biederer_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_biederer_;
array[1..mbr_nScs_ToWeighted_RefTo_biederer_] of var bool: mbr_valuations_ToWeighted_RefTo_biederer_;
int: mbr_top_ToWeighted_RefTo_biederer_ = 0;
constraint mbr_overall_ToWeighted_RefTo_biederer_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_biederer_,mbr_nScs_ToWeighted_RefTo_biederer_, mbr_k_ToWeighted_RefTo_biederer_, mbr_weights_ToWeighted_RefTo_biederer_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_biederer_[1] = (worksAt[biederer] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_biederer_[2] = (worksAt[biederer] = msg);
constraint mbr_valuations_ToWeighted_RefTo_biederer_[3] = (worksAt[biederer] = sie);
constraint mbr_valuations_ToWeighted_RefTo_biederer_[4] = (worksAt[biederer] = qpl);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_geiger_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_geiger_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_geiger_ : mbr_crEdges_ToWeighted_RefTo_geiger_ = [|1, 3 | 2, 4 | 3, 9 | 4, 7 | 6, 2 | 7, 1 | 8, 10 | 9, 5 | 10, 6 | 1, 9 | 2, 7 | 3, 5 | 4, 1 | 6, 4 | 6, 7 | 7, 3 | 7, 9 | 8, 6 | 10, 2 | 10, 4 | 10, 7 | 1, 5 | 2, 1 | 2, 3 | 2, 9 | 4, 3 | 4, 5 | 4, 9 | 6, 1 | 6, 3 | 6, 5 | 6, 9 | 7, 5 | 8, 1 | 8, 2 | 8, 3 | 8, 4 | 8, 5 | 8, 7 | 8, 9 | 10, 1 | 10, 3 | 10, 5 | 10, 9 | 2, 5|]; 
bool : mbr_useSPD_ToWeighted_RefTo_geiger_ = true; 
int : mbr_k_ToWeighted_RefTo_geiger_ = mbr_nScs_ToWeighted_RefTo_geiger_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_geiger_) (mbr_weights_ToWeighted_RefTo_geiger_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_geiger_] of int : mbr_weights_ToWeighted_RefTo_geiger_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_geiger_, mbr_crEdges_ToWeighted_RefTo_geiger_, mbr_useSPD_ToWeighted_RefTo_geiger_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_geiger_;
array[1..mbr_nScs_ToWeighted_RefTo_geiger_] of var bool: mbr_valuations_ToWeighted_RefTo_geiger_;
int: mbr_top_ToWeighted_RefTo_geiger_ = 0;
constraint mbr_overall_ToWeighted_RefTo_geiger_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_geiger_,mbr_nScs_ToWeighted_RefTo_geiger_, mbr_k_ToWeighted_RefTo_geiger_, mbr_weights_ToWeighted_RefTo_geiger_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_geiger_[1] = (worksAt[geiger] = ora);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[2] = (worksAt[geiger] = msg);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[3] = (worksAt[geiger] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[4] = (worksAt[geiger] = zue);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[5] = (worksAt[geiger] = sie);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[6] = (worksAt[geiger] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[7] = (worksAt[geiger] = mai);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[8] = (worksAt[geiger] = sap);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[9] = (worksAt[geiger] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_geiger_[10] = (worksAt[geiger] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_sandstede_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_sandstede_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_sandstede_ : mbr_crEdges_ToWeighted_RefTo_sandstede_ = [|2, 6 | 3, 5 | 4, 3 | 5, 1 | 6, 9 | 7, 4 | 8, 2 | 9, 7 | 10, 8 | 2, 9 | 3, 1 | 4, 1 | 4, 5 | 6, 7 | 7, 1 | 7, 3 | 7, 5 | 8, 6 | 8, 9 | 9, 1 | 9, 3 | 9, 4 | 9, 5 | 10, 2 | 10, 6 | 10, 9 | 2, 1 | 2, 3 | 2, 4 | 2, 5 | 2, 7 | 6, 1 | 6, 3 | 6, 4 | 6, 5 | 8, 1 | 8, 3 | 8, 4 | 8, 5 | 8, 7 | 10, 1 | 10, 3 | 10, 4 | 10, 5 | 10, 7|]; 
bool : mbr_useSPD_ToWeighted_RefTo_sandstede_ = true; 
int : mbr_k_ToWeighted_RefTo_sandstede_ = mbr_nScs_ToWeighted_RefTo_sandstede_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_sandstede_) (mbr_weights_ToWeighted_RefTo_sandstede_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_sandstede_] of int : mbr_weights_ToWeighted_RefTo_sandstede_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_sandstede_, mbr_crEdges_ToWeighted_RefTo_sandstede_, mbr_useSPD_ToWeighted_RefTo_sandstede_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_sandstede_;
array[1..mbr_nScs_ToWeighted_RefTo_sandstede_] of var bool: mbr_valuations_ToWeighted_RefTo_sandstede_;
int: mbr_top_ToWeighted_RefTo_sandstede_ = 0;
constraint mbr_overall_ToWeighted_RefTo_sandstede_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_sandstede_,mbr_nScs_ToWeighted_RefTo_sandstede_, mbr_k_ToWeighted_RefTo_sandstede_, mbr_weights_ToWeighted_RefTo_sandstede_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[1] = (worksAt[sandstede] = ora);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[2] = (worksAt[sandstede] = msg);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[3] = (worksAt[sandstede] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[4] = (worksAt[sandstede] = zue);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[5] = (worksAt[sandstede] = sie);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[6] = (worksAt[sandstede] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[7] = (worksAt[sandstede] = mai);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[8] = (worksAt[sandstede] = sap);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[9] = (worksAt[sandstede] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_sandstede_[10] = (worksAt[sandstede] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_rieger_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_rieger_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_rieger_ : mbr_crEdges_ToWeighted_RefTo_rieger_ = [|1, 4 | 2, 1 | 3, 8 | 5, 2 | 6, 5 | 7, 3 | 8, 9 | 9, 6 | 10, 7 | 2, 4 | 3, 9 | 5, 1 | 5, 4 | 6, 1 | 6, 2 | 6, 4 | 7, 8 | 7, 9 | 8, 6 | 9, 1 | 9, 2 | 9, 4 | 9, 5 | 10, 3 | 10, 8 | 10, 9 | 3, 1 | 3, 2 | 3, 4 | 3, 5 | 3, 6 | 7, 1 | 7, 2 | 7, 4 | 7, 5 | 7, 6 | 8, 1 | 8, 2 | 8, 4 | 8, 5 | 10, 1 | 10, 2 | 10, 4 | 10, 5 | 10, 6|]; 
bool : mbr_useSPD_ToWeighted_RefTo_rieger_ = true; 
int : mbr_k_ToWeighted_RefTo_rieger_ = mbr_nScs_ToWeighted_RefTo_rieger_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_rieger_) (mbr_weights_ToWeighted_RefTo_rieger_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_rieger_] of int : mbr_weights_ToWeighted_RefTo_rieger_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_rieger_, mbr_crEdges_ToWeighted_RefTo_rieger_, mbr_useSPD_ToWeighted_RefTo_rieger_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_rieger_;
array[1..mbr_nScs_ToWeighted_RefTo_rieger_] of var bool: mbr_valuations_ToWeighted_RefTo_rieger_;
int: mbr_top_ToWeighted_RefTo_rieger_ = 0;
constraint mbr_overall_ToWeighted_RefTo_rieger_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_rieger_,mbr_nScs_ToWeighted_RefTo_rieger_, mbr_k_ToWeighted_RefTo_rieger_, mbr_weights_ToWeighted_RefTo_rieger_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_rieger_[1] = (worksAt[rieger] = ora);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[2] = (worksAt[rieger] = msg);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[3] = (worksAt[rieger] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[4] = (worksAt[rieger] = zue);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[5] = (worksAt[rieger] = sie);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[6] = (worksAt[rieger] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[7] = (worksAt[rieger] = mai);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[8] = (worksAt[rieger] = sap);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[9] = (worksAt[rieger] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_rieger_[10] = (worksAt[rieger] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_sosnowski_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_sosnowski_ = 9; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_sosnowski_ : mbr_crEdges_ToWeighted_RefTo_sosnowski_ = [|1, 2 | 2, 4 | 4, 3 | 5, 1 | 6, 5 | 7, 6 | 8, 7 | 9, 8 | 1, 4 | 2, 3 | 5, 2 | 5, 4 | 6, 1 | 6, 2 | 6, 4 | 7, 1 | 7, 2 | 7, 4 | 7, 5 | 8, 1 | 8, 2 | 8, 4 | 8, 5 | 8, 6 | 9, 1 | 9, 2 | 9, 4 | 9, 5 | 9, 6 | 9, 7 | 1, 3 | 5, 3 | 6, 3 | 7, 3 | 8, 3 | 9, 3|]; 
bool : mbr_useSPD_ToWeighted_RefTo_sosnowski_ = true; 
int : mbr_k_ToWeighted_RefTo_sosnowski_ = mbr_nScs_ToWeighted_RefTo_sosnowski_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_sosnowski_) (mbr_weights_ToWeighted_RefTo_sosnowski_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_sosnowski_] of int : mbr_weights_ToWeighted_RefTo_sosnowski_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_sosnowski_, mbr_crEdges_ToWeighted_RefTo_sosnowski_, mbr_useSPD_ToWeighted_RefTo_sosnowski_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_sosnowski_;
array[1..mbr_nScs_ToWeighted_RefTo_sosnowski_] of var bool: mbr_valuations_ToWeighted_RefTo_sosnowski_;
int: mbr_top_ToWeighted_RefTo_sosnowski_ = 0;
constraint mbr_overall_ToWeighted_RefTo_sosnowski_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_sosnowski_,mbr_nScs_ToWeighted_RefTo_sosnowski_, mbr_k_ToWeighted_RefTo_sosnowski_, mbr_weights_ToWeighted_RefTo_sosnowski_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[1] = (worksAt[sosnowski] = mai);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[2] = (worksAt[sosnowski] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[3] = (worksAt[sosnowski] = msg);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[4] = (worksAt[sosnowski] = zue);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[5] = (worksAt[sosnowski] = ora);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[6] = (worksAt[sosnowski] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[7] = (worksAt[sosnowski] = sie);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[8] = (worksAt[sosnowski] = sap);
constraint mbr_valuations_ToWeighted_RefTo_sosnowski_[9] = (worksAt[sosnowski] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_goetz_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_goetz_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_goetz_ : mbr_crEdges_ToWeighted_RefTo_goetz_ = [|1, 8 | 2, 9 | 3, 6 | 4, 2 | 5, 3 | 6, 7 | 8, 4 | 9, 10 | 10, 5 | 1, 4 | 2, 10 | 3, 7 | 4, 9 | 4, 10 | 5, 6 | 5, 7 | 8, 2 | 8, 9 | 8, 10 | 9, 5 | 10, 3 | 10, 6 | 10, 7 | 1, 2 | 1, 9 | 1, 10 | 2, 3 | 2, 5 | 2, 6 | 2, 7 | 4, 3 | 4, 5 | 4, 6 | 4, 7 | 8, 3 | 8, 5 | 8, 6 | 8, 7 | 9, 3 | 9, 6 | 9, 7 | 1, 3 | 1, 5 | 1, 6 | 1, 7|]; 
bool : mbr_useSPD_ToWeighted_RefTo_goetz_ = true; 
int : mbr_k_ToWeighted_RefTo_goetz_ = mbr_nScs_ToWeighted_RefTo_goetz_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_goetz_) (mbr_weights_ToWeighted_RefTo_goetz_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_goetz_] of int : mbr_weights_ToWeighted_RefTo_goetz_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_goetz_, mbr_crEdges_ToWeighted_RefTo_goetz_, mbr_useSPD_ToWeighted_RefTo_goetz_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_goetz_;
array[1..mbr_nScs_ToWeighted_RefTo_goetz_] of var bool: mbr_valuations_ToWeighted_RefTo_goetz_;
int: mbr_top_ToWeighted_RefTo_goetz_ = 0;
constraint mbr_overall_ToWeighted_RefTo_goetz_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_goetz_,mbr_nScs_ToWeighted_RefTo_goetz_, mbr_k_ToWeighted_RefTo_goetz_, mbr_weights_ToWeighted_RefTo_goetz_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_goetz_[1] = (worksAt[goetz] = ora);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[2] = (worksAt[goetz] = msg);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[3] = (worksAt[goetz] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[4] = (worksAt[goetz] = zue);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[5] = (worksAt[goetz] = sie);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[6] = (worksAt[goetz] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[7] = (worksAt[goetz] = mai);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[8] = (worksAt[goetz] = sap);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[9] = (worksAt[goetz] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_goetz_[10] = (worksAt[goetz] = cap);

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_mueller_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_mueller_ = 10; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_mueller_ : mbr_crEdges_ToWeighted_RefTo_mueller_ = [|1, 7 | 2, 5 | 4, 6 | 5, 3 | 6, 10 | 7, 2 | 8, 9 | 9, 1 | 10, 8 | 1, 2 | 2, 3 | 4, 10 | 6, 8 | 7, 3 | 7, 5 | 8, 1 | 9, 2 | 9, 7 | 10, 1 | 10, 9 | 1, 3 | 1, 5 | 4, 1 | 4, 8 | 4, 9 | 6, 1 | 6, 9 | 8, 2 | 8, 3 | 8, 5 | 8, 7 | 9, 3 | 9, 5 | 10, 2 | 10, 3 | 10, 5 | 10, 7 | 4, 2 | 4, 3 | 4, 5 | 4, 7 | 6, 2 | 6, 3 | 6, 5 | 6, 7|]; 
bool : mbr_useSPD_ToWeighted_RefTo_mueller_ = true; 
int : mbr_k_ToWeighted_RefTo_mueller_ = mbr_nScs_ToWeighted_RefTo_mueller_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_mueller_) (mbr_weights_ToWeighted_RefTo_mueller_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_mueller_] of int : mbr_weights_ToWeighted_RefTo_mueller_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_mueller_, mbr_crEdges_ToWeighted_RefTo_mueller_, mbr_useSPD_ToWeighted_RefTo_mueller_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_mueller_;
array[1..mbr_nScs_ToWeighted_RefTo_mueller_] of var bool: mbr_valuations_ToWeighted_RefTo_mueller_;
int: mbr_top_ToWeighted_RefTo_mueller_ = 0;
constraint mbr_overall_ToWeighted_RefTo_mueller_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_mueller_,mbr_nScs_ToWeighted_RefTo_mueller_, mbr_k_ToWeighted_RefTo_mueller_, mbr_weights_ToWeighted_RefTo_mueller_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_mueller_[1] = (worksAt[mueller] = ora);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[2] = (worksAt[mueller] = msg);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[3] = (worksAt[mueller] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[4] = (worksAt[mueller] = zue);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[5] = (worksAt[mueller] = sie);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[6] = (worksAt[mueller] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[7] = (worksAt[mueller] = mai);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[8] = (worksAt[mueller] = sap);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[9] = (worksAt[mueller] = kuk);
constraint mbr_valuations_ToWeighted_RefTo_mueller_[10] = (worksAt[mueller] = cap);
var int: mbr_overall_MBR_VOT_1;
constraint mbr_overall_MBR_VOT_1 = 
sum([
mbr_overall_ToWeighted_RefTo_anneser_, mbr_overall_brandt, mbr_overall_ToWeighted_RefTo_martin_, mbr_overall_ToWeighted_RefTo_alhomssi_, mbr_overall_ToWeighted_RefTo_beeh_, mbr_overall_ToWeighted_RefTo_schroll_, mbr_overall_ToWeighted_RefTo_keppner_, mbr_overall_ToWeighted_RefTo_henrich_, mbr_overall_ToWeighted_RefTo_pichlmair_, mbr_overall_ToWeighted_RefTo_schwarzmeier_, mbr_overall_ToWeighted_RefTo_biederer_, mbr_overall_ToWeighted_RefTo_geiger_, mbr_overall_ToWeighted_RefTo_sandstede_, mbr_overall_ToWeighted_RefTo_rieger_, mbr_overall_ToWeighted_RefTo_sosnowski_, mbr_overall_ToWeighted_RefTo_goetz_, mbr_overall_ToWeighted_RefTo_mueller_])
 ;

% ---------------------------------------------------
%   PVS ToWeighted_RefTo_companies_
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_ToWeighted_RefTo_companies_ = 43; 
array[int, 1..2] of 1..mbr_nScs_ToWeighted_RefTo_companies_ : mbr_crEdges_ToWeighted_RefTo_companies_ = [|4, 1 | 4, 2 | 4, 3 | 5, 1 | 5, 2 | 5, 3 | 6, 1 | 6, 2 | 6, 3 | 8, 7 | 9, 8 | 10, 9 | 11, 10 | 12, 11 | 9, 7 | 10, 7 | 10, 8 | 11, 7 | 11, 8 | 11, 9 | 12, 7 | 12, 8 | 12, 9 | 12, 10|]; 
bool : mbr_useSPD_ToWeighted_RefTo_companies_ = false; 
int : mbr_k_ToWeighted_RefTo_companies_ = mbr_nScs_ToWeighted_RefTo_companies_ * max(i in 1..mbr_nScs_ToWeighted_RefTo_companies_) (mbr_weights_ToWeighted_RefTo_companies_[i]) ; 
array[1..mbr_nScs_ToWeighted_RefTo_companies_] of int : mbr_weights_ToWeighted_RefTo_companies_ = calculate_cr_weights(mbr_nScs_ToWeighted_RefTo_companies_, mbr_crEdges_ToWeighted_RefTo_companies_, mbr_useSPD_ToWeighted_RefTo_companies_); 

% Decision variables: 
var int: mbr_overall_ToWeighted_RefTo_companies_;
array[1..mbr_nScs_ToWeighted_RefTo_companies_] of var bool: mbr_valuations_ToWeighted_RefTo_companies_;
int: mbr_top_ToWeighted_RefTo_companies_ = 0;
constraint mbr_overall_ToWeighted_RefTo_companies_ = weighted_sum (mbr_valuations_ToWeighted_RefTo_companies_,mbr_nScs_ToWeighted_RefTo_companies_, mbr_k_ToWeighted_RefTo_companies_, mbr_weights_ToWeighted_RefTo_companies_);

% MiniSearch predicates: 

% Soft constraints: 
constraint mbr_valuations_ToWeighted_RefTo_companies_[1] = (worksAt[biederer] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[2] = (worksAt[keppner] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[3] = (worksAt[mueller] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[4] = (worksAt[beeh] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[5] = (worksAt[sandstede] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[6] = (worksAt[goetz] = cap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[7] = (worksAt[sandstede] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[8] = (worksAt[schwarzmeier] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[9] = (worksAt[goetz] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[10] = (worksAt[keppner] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[11] = (worksAt[henrich] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[12] = (worksAt[schroll] = qpl);
constraint mbr_valuations_ToWeighted_RefTo_companies_[13] = (worksAt[brandt] = mai);
constraint mbr_valuations_ToWeighted_RefTo_companies_[14] = (worksAt[geiger] = mai);
constraint mbr_valuations_ToWeighted_RefTo_companies_[15] = (worksAt[keppner] = mai);
constraint mbr_valuations_ToWeighted_RefTo_companies_[16] = (worksAt[schwarzmeier] = mai);
constraint mbr_valuations_ToWeighted_RefTo_companies_[17] = (worksAt[goetz] = mai);
constraint mbr_valuations_ToWeighted_RefTo_companies_[18] = (worksAt[beeh] = sie);
constraint mbr_valuations_ToWeighted_RefTo_companies_[19] = (worksAt[mueller] = sie);
constraint mbr_valuations_ToWeighted_RefTo_companies_[20] = (worksAt[rieger] = sie);
constraint mbr_valuations_ToWeighted_RefTo_companies_[21] = (worksAt[keppner] = sie);
constraint mbr_valuations_ToWeighted_RefTo_companies_[22] = (worksAt[schroll] = sie);
constraint mbr_valuations_ToWeighted_RefTo_companies_[23] = (worksAt[beeh] = sap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[24] = (worksAt[geiger] = sap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[25] = (worksAt[goetz] = sap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[26] = (worksAt[martin] = sap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[27] = (worksAt[sandstede] = sap);
constraint mbr_valuations_ToWeighted_RefTo_companies_[28] = (worksAt[anneser] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[29] = (worksAt[brandt] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[30] = (worksAt[geiger] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[31] = (worksAt[henrich] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[32] = (worksAt[sandstede] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[33] = (worksAt[schwarzmeier] = zue);
constraint mbr_valuations_ToWeighted_RefTo_companies_[34] = (worksAt[anneser] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[35] = (worksAt[brandt] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[36] = (worksAt[mueller] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[37] = (worksAt[pichlmair] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[38] = (worksAt[sandstede] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[39] = (worksAt[schwarzmeier] = ora);
constraint mbr_valuations_ToWeighted_RefTo_companies_[40] = (worksAt[anneser] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_companies_[41] = (worksAt[biederer] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_companies_[42] = (worksAt[mueller] = bmw);
constraint mbr_valuations_ToWeighted_RefTo_companies_[43] = (worksAt[goetz] = bmw);

function ann: mbr_postGetBetter_MBR_LEX_2() = post(( (
sum([
mbr_overall_ToWeighted_RefTo_anneser_, mbr_overall_brandt, mbr_overall_ToWeighted_RefTo_martin_, mbr_overall_ToWeighted_RefTo_alhomssi_, mbr_overall_ToWeighted_RefTo_beeh_, mbr_overall_ToWeighted_RefTo_schroll_, mbr_overall_ToWeighted_RefTo_keppner_, mbr_overall_ToWeighted_RefTo_henrich_, mbr_overall_ToWeighted_RefTo_pichlmair_, mbr_overall_ToWeighted_RefTo_schwarzmeier_, mbr_overall_ToWeighted_RefTo_biederer_, mbr_overall_ToWeighted_RefTo_geiger_, mbr_overall_ToWeighted_RefTo_sandstede_, mbr_overall_ToWeighted_RefTo_rieger_, mbr_overall_ToWeighted_RefTo_sosnowski_, mbr_overall_ToWeighted_RefTo_goetz_, mbr_overall_ToWeighted_RefTo_mueller_])
< 

sum([sol(mbr_overall_ToWeighted_RefTo_anneser_), sol(mbr_overall_brandt), sol(mbr_overall_ToWeighted_RefTo_martin_), sol(mbr_overall_ToWeighted_RefTo_alhomssi_), sol(mbr_overall_ToWeighted_RefTo_beeh_), sol(mbr_overall_ToWeighted_RefTo_schroll_), sol(mbr_overall_ToWeighted_RefTo_keppner_), sol(mbr_overall_ToWeighted_RefTo_henrich_), sol(mbr_overall_ToWeighted_RefTo_pichlmair_), sol(mbr_overall_ToWeighted_RefTo_schwarzmeier_), sol(mbr_overall_ToWeighted_RefTo_biederer_), sol(mbr_overall_ToWeighted_RefTo_geiger_), sol(mbr_overall_ToWeighted_RefTo_sandstede_), sol(mbr_overall_ToWeighted_RefTo_rieger_), sol(mbr_overall_ToWeighted_RefTo_sosnowski_), sol(mbr_overall_ToWeighted_RefTo_goetz_), sol(mbr_overall_ToWeighted_RefTo_mueller_)])
) \/ ( (((sol(mbr_overall_ToWeighted_RefTo_anneser_) = mbr_overall_ToWeighted_RefTo_anneser_) /\ (sol(mbr_overall_brandt) = mbr_overall_brandt) /\ (sol(mbr_overall_ToWeighted_RefTo_martin_) = mbr_overall_ToWeighted_RefTo_martin_) /\ (sol(mbr_overall_ToWeighted_RefTo_alhomssi_) = mbr_overall_ToWeighted_RefTo_alhomssi_) /\ (sol(mbr_overall_ToWeighted_RefTo_beeh_) = mbr_overall_ToWeighted_RefTo_beeh_) /\ (sol(mbr_overall_ToWeighted_RefTo_schroll_) = mbr_overall_ToWeighted_RefTo_schroll_) /\ (sol(mbr_overall_ToWeighted_RefTo_keppner_) = mbr_overall_ToWeighted_RefTo_keppner_) /\ (sol(mbr_overall_ToWeighted_RefTo_henrich_) = mbr_overall_ToWeighted_RefTo_henrich_) /\ (sol(mbr_overall_ToWeighted_RefTo_pichlmair_) = mbr_overall_ToWeighted_RefTo_pichlmair_) /\ (sol(mbr_overall_ToWeighted_RefTo_schwarzmeier_) = mbr_overall_ToWeighted_RefTo_schwarzmeier_) /\ (sol(mbr_overall_ToWeighted_RefTo_biederer_) = mbr_overall_ToWeighted_RefTo_biederer_) /\ (sol(mbr_overall_ToWeighted_RefTo_geiger_) = mbr_overall_ToWeighted_RefTo_geiger_) /\ (sol(mbr_overall_ToWeighted_RefTo_sandstede_) = mbr_overall_ToWeighted_RefTo_sandstede_) /\ (sol(mbr_overall_ToWeighted_RefTo_rieger_) = mbr_overall_ToWeighted_RefTo_rieger_) /\ (sol(mbr_overall_ToWeighted_RefTo_sosnowski_) = mbr_overall_ToWeighted_RefTo_sosnowski_) /\ (sol(mbr_overall_ToWeighted_RefTo_goetz_) = mbr_overall_ToWeighted_RefTo_goetz_) /\ (sol(mbr_overall_ToWeighted_RefTo_mueller_) = mbr_overall_ToWeighted_RefTo_mueller_))) /\ is_worse_weighted(sol(mbr_overall_ToWeighted_RefTo_companies_), mbr_overall_ToWeighted_RefTo_companies_, mbr_nScs_ToWeighted_RefTo_companies_, mbr_k_ToWeighted_RefTo_companies_, mbr_weights_ToWeighted_RefTo_companies_)) ));

function ann: mbr_postNotGetWorse_MBR_LEX_2() = post(not ( (sol(mbr_overall_ToWeighted_RefTo_anneser_) = mbr_overall_ToWeighted_RefTo_anneser_ /\ sol(mbr_overall_brandt) = mbr_overall_brandt /\ sol(mbr_overall_ToWeighted_RefTo_martin_) = mbr_overall_ToWeighted_RefTo_martin_ /\ sol(mbr_overall_ToWeighted_RefTo_alhomssi_) = mbr_overall_ToWeighted_RefTo_alhomssi_ /\ sol(mbr_overall_ToWeighted_RefTo_beeh_) = mbr_overall_ToWeighted_RefTo_beeh_ /\ sol(mbr_overall_ToWeighted_RefTo_schroll_) = mbr_overall_ToWeighted_RefTo_schroll_ /\ sol(mbr_overall_ToWeighted_RefTo_keppner_) = mbr_overall_ToWeighted_RefTo_keppner_ /\ sol(mbr_overall_ToWeighted_RefTo_henrich_) = mbr_overall_ToWeighted_RefTo_henrich_ /\ sol(mbr_overall_ToWeighted_RefTo_pichlmair_) = mbr_overall_ToWeighted_RefTo_pichlmair_ /\ sol(mbr_overall_ToWeighted_RefTo_schwarzmeier_) = mbr_overall_ToWeighted_RefTo_schwarzmeier_ /\ sol(mbr_overall_ToWeighted_RefTo_biederer_) = mbr_overall_ToWeighted_RefTo_biederer_ /\ sol(mbr_overall_ToWeighted_RefTo_geiger_) = mbr_overall_ToWeighted_RefTo_geiger_ /\ sol(mbr_overall_ToWeighted_RefTo_sandstede_) = mbr_overall_ToWeighted_RefTo_sandstede_ /\ sol(mbr_overall_ToWeighted_RefTo_rieger_) = mbr_overall_ToWeighted_RefTo_rieger_ /\ sol(mbr_overall_ToWeighted_RefTo_sosnowski_) = mbr_overall_ToWeighted_RefTo_sosnowski_ /\ sol(mbr_overall_ToWeighted_RefTo_goetz_) = mbr_overall_ToWeighted_RefTo_goetz_ /\ sol(mbr_overall_ToWeighted_RefTo_mueller_) = mbr_overall_ToWeighted_RefTo_mueller_ /\ sol(mbr_overall_ToWeighted_RefTo_companies_) = mbr_overall_ToWeighted_RefTo_companies_) \/ (( (is_worse_weighted(sol(mbr_overall_ToWeighted_RefTo_companies_), mbr_overall_ToWeighted_RefTo_companies_, mbr_nScs_ToWeighted_RefTo_companies_, mbr_k_ToWeighted_RefTo_companies_, mbr_weights_ToWeighted_RefTo_companies_)) \/ ( (sol(mbr_overall_ToWeighted_RefTo_companies_) = mbr_overall_ToWeighted_RefTo_companies_) /\ 
sum([
mbr_overall_ToWeighted_RefTo_anneser_, mbr_overall_brandt, mbr_overall_ToWeighted_RefTo_martin_, mbr_overall_ToWeighted_RefTo_alhomssi_, mbr_overall_ToWeighted_RefTo_beeh_, mbr_overall_ToWeighted_RefTo_schroll_, mbr_overall_ToWeighted_RefTo_keppner_, mbr_overall_ToWeighted_RefTo_henrich_, mbr_overall_ToWeighted_RefTo_pichlmair_, mbr_overall_ToWeighted_RefTo_schwarzmeier_, mbr_overall_ToWeighted_RefTo_biederer_, mbr_overall_ToWeighted_RefTo_geiger_, mbr_overall_ToWeighted_RefTo_sandstede_, mbr_overall_ToWeighted_RefTo_rieger_, mbr_overall_ToWeighted_RefTo_sosnowski_, mbr_overall_ToWeighted_RefTo_goetz_, mbr_overall_ToWeighted_RefTo_mueller_])
< 

sum([sol(mbr_overall_ToWeighted_RefTo_anneser_), sol(mbr_overall_brandt), sol(mbr_overall_ToWeighted_RefTo_martin_), sol(mbr_overall_ToWeighted_RefTo_alhomssi_), sol(mbr_overall_ToWeighted_RefTo_beeh_), sol(mbr_overall_ToWeighted_RefTo_schroll_), sol(mbr_overall_ToWeighted_RefTo_keppner_), sol(mbr_overall_ToWeighted_RefTo_henrich_), sol(mbr_overall_ToWeighted_RefTo_pichlmair_), sol(mbr_overall_ToWeighted_RefTo_schwarzmeier_), sol(mbr_overall_ToWeighted_RefTo_biederer_), sol(mbr_overall_ToWeighted_RefTo_geiger_), sol(mbr_overall_ToWeighted_RefTo_sandstede_), sol(mbr_overall_ToWeighted_RefTo_rieger_), sol(mbr_overall_ToWeighted_RefTo_sosnowski_), sol(mbr_overall_ToWeighted_RefTo_goetz_), sol(mbr_overall_ToWeighted_RefTo_mueller_)])
) ))));
output [ "worksAt = \(worksAt), mbr_overall_MBR_VOT_1 = \(mbr_overall_MBR_VOT_1)"] ++ [ "\nValuations: mbr_overall_ToWeighted_RefTo_anneser_ = \(mbr_overall_ToWeighted_RefTo_anneser_); mbr_overall_brandt = \(mbr_overall_brandt); mbr_overall_ToWeighted_RefTo_martin_ = \(mbr_overall_ToWeighted_RefTo_martin_); mbr_overall_ToWeighted_RefTo_alhomssi_ = \(mbr_overall_ToWeighted_RefTo_alhomssi_); mbr_overall_ToWeighted_RefTo_beeh_ = \(mbr_overall_ToWeighted_RefTo_beeh_); mbr_overall_ToWeighted_RefTo_schroll_ = \(mbr_overall_ToWeighted_RefTo_schroll_); mbr_overall_ToWeighted_RefTo_keppner_ = \(mbr_overall_ToWeighted_RefTo_keppner_); mbr_overall_ToWeighted_RefTo_henrich_ = \(mbr_overall_ToWeighted_RefTo_henrich_); mbr_overall_ToWeighted_RefTo_pichlmair_ = \(mbr_overall_ToWeighted_RefTo_pichlmair_); mbr_overall_ToWeighted_RefTo_schwarzmeier_ = \(mbr_overall_ToWeighted_RefTo_schwarzmeier_); mbr_overall_ToWeighted_RefTo_biederer_ = \(mbr_overall_ToWeighted_RefTo_biederer_); mbr_overall_ToWeighted_RefTo_geiger_ = \(mbr_overall_ToWeighted_RefTo_geiger_); mbr_overall_ToWeighted_RefTo_sandstede_ = \(mbr_overall_ToWeighted_RefTo_sandstede_); mbr_overall_ToWeighted_RefTo_rieger_ = \(mbr_overall_ToWeighted_RefTo_rieger_); mbr_overall_ToWeighted_RefTo_sosnowski_ = \(mbr_overall_ToWeighted_RefTo_sosnowski_); mbr_overall_ToWeighted_RefTo_goetz_ = \(mbr_overall_ToWeighted_RefTo_goetz_); mbr_overall_ToWeighted_RefTo_mueller_ = \(mbr_overall_ToWeighted_RefTo_mueller_); mbr_overall_ToWeighted_RefTo_companies_ = \(mbr_overall_ToWeighted_RefTo_companies_)\n"];


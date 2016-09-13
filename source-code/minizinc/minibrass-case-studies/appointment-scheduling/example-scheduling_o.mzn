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
%   PVS students
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_students = 6; 
int : mbr_k_students = 100; 
array[1..mbr_nScs_students] of int : mbr_weights_students = [1, 1, 1, 1, 1, 101]; 

% Decision variables: 
var 0..mbr_k_students: mbr_overall_students;
array[1..mbr_nScs_students] of var bool: mbr_valuations_students;
par 0..mbr_k_students: mbr_top_students = 0;

% MiniSearch predicates: 
constraint mbr_overall_students = weighted_sum (mbr_valuations_students,mbr_nScs_students, mbr_k_students, mbr_weights_students);

% Soft constraints: 
constraint mbr_valuations_students[1] = (scheduled[raubholz] in {monday, tuesday});
constraint mbr_valuations_students[2] = (scheduled[schraubale] in {tuesday, wednesday});
constraint mbr_valuations_students[3] = (scheduled[meerfluss] in {tuesday});
constraint mbr_valuations_students[4] = (scheduled[gleich] in {monday, tuesday});
constraint mbr_valuations_students[5] = (scheduled[lustig] in {monday, wednesday});
constraint mbr_valuations_students[6] = (scheduled[lustig] != tuesday);

% ---------------------------------------------------
%   PVS teachers
% ---------------------------------------------------
% Parameters: 
int : mbr_nScs_teachers = 1; 
int : mbr_k_teachers = 1000; 

% Decision variables: 
var 0..mbr_k_teachers: mbr_overall_teachers;
array[1..mbr_nScs_teachers] of var 0..mbr_k_teachers: mbr_valuations_teachers;
par 0..mbr_k_teachers: mbr_top_teachers = 0;

% MiniSearch predicates: 
constraint mbr_overall_teachers = sum (mbr_valuations_teachers,mbr_nScs_teachers, mbr_k_teachers);

% Soft constraints: 
constraint mbr_valuations_teachers[1] = (scheduledDates);

function ann: mbr_postBetter_MBR_LEX_1() = post(( (is_worse_weighted(sol(mbr_overall_students), mbr_overall_students, mbr_nScs_students, mbr_k_students, mbr_weights_students)) \/ (sol(mbr_overall_students) = mbr_overall_students /\ is_worse_weighted(sol(mbr_overall_teachers), mbr_overall_teachers, mbr_nScs_teachers, mbr_k_teachers)) ));

% Add this line to your output to make use of minisearch
% [ "\nValuations: mbr_overall_students = \(mbr_overall_students); mbr_overall_teachers = \(mbr_overall_teachers)\n"]

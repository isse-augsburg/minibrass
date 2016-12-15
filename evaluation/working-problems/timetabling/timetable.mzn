% 
% Timetable problem in MiniZinc.
% 
% From the Alma-0 program timetable.a0
% """
% The problem is from 
% @Conference{Scha97,
%   author =       "Andrea Schaerf",
%   title =        "Combining Local Search and Look-Ahead for Scheduling
%                   and Constraint Satisfaction Problems",
%   booktitle =     "Proc.\ of the 15th International Joint Conf.\ on
%                   Artificial Intelligence (IJCAI-96)",
%   address =      "Nagoya, Japan",
%   year =         "1997",
%   pages =        "1254--1259",
%   publisher =    "Morgan Kaufmann",
% }
%
% There are q courses, and each course i consists of
% k_i lectures, and p periods 1,...,p. For all i = 1,...,q, all lectures
% l = 1,...,k_i must be assigned to a period j in such a way that the
% following constraints are satisfied:
%
% 1. Conflicts: There is a conflict matrix M such that M[i,j] = 1 if
%    courses i and j have common students. Lectures of courses i and j  must
%    be all scheduled at different times
%
% 2. Availabilities: There is an availability binary matrix A such that
%    A[i,j] = 1 then lectures of course i cannot be scheduled at period j.
%
% 3. Rooms: There are r rooms available. At most r lectures can be
%    scheduled at period k, for each k = 1,...,p.
% """
% 
% This MiniZinc model was created by Hakan Kjellerstrand, hakank@gmail.com
% See also my MiniZinc page: http://www.hakank.org/minizinc/

% Alexander Schiendorfer 
% 2016-12-14; Modified for MiniBrass 
%

include "globals.mzn"; 

include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_consistency.mzn";

include "evaluation-config.mzn";
% include PVS-specific libraries 
include "evaluation-pvs.mzn";

include "evaluation-heuristics.mzn";

include "evaluation-search.mzn";

array[int, 1..2] of int: crTempEdges;
crEdges = transClosure(SOFTCONSTRAINTS, crTempEdges);
nCrEdges = max(crEdges);

% include search heuristic to be used with model 
ann: custSearch = int_search([timetable[c,p] | c in 1..courses, p in 1..periods], 
                     most_constrained, indomain_split, complete) ;

array[int] of var int: lnsVars = [ timetable[i,j] | i in 1..courses, j in 1..periods] ;


int: courses; set of int: COURSE = 1..courses;
int: periods;
int: rooms;

array[1..courses,1..periods] of int: available;
array[int, 1..2] of COURSE: conflicts;

array[1..courses] of int: requirement;

% decision variables
array[1..courses,1..periods] of var 0..1: timetable;


% solve satisfy;
constraint
   % 1. Conflicts: There is a conflict matrix M such that M[i,j] = 1 if
   %    courses i and j have common students. Lectures of courses i and j  must
   %    be all scheduled at different times
   forall(i in index_set_1of2(conflicts)) ( 
     forall(p in 1..periods) (
       timetable[conflicts[i,1],p] + timetable[conflicts[i,2], p] <= 1
     ) 
   )

   %
   % 2. Availabilities: There is an availability binary matrix A such that
   %    A[i,j]= 1 then lectures of course i cannot be scheduled at period j.
   %    [Note: It must be that if A[i,j] = 0 then the lectures cannot be 
   %     scheduled at period j.]
   /\
   forall(c in 1..courses, p in 1..periods) (
        if available[c,p] = 0 then
           timetable[c,p] = 0
        else
           true
        endif
   )

   % 3. Rooms: There are r rooms available. At most r lectures can be
   %    scheduled at period k, for each k = 1,...,p.
   /\
   forall(p in 1..periods) (
     sum([timetable[c,p] | c in 1..courses]) <= rooms
   )

   % The number of lectures per course
   /\
   forall(c in 1..courses) (
     sum([timetable[c,p] | p in 1..periods]) = requirement[c]
   )

;

output [
    "\nOBJ = \(penSum)\n",
    "timetable = ", show(timetable), "\n",
    "penSum = ", show(penSum), "\n",
    "penalties = ", show(penalties), "\n",
    "violatedScs = ", show(violatedScs), "\n",
    "soft constraints = ", show(satisfied), "\n"
];




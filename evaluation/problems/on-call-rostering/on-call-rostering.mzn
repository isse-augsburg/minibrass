%----------------------------------------------------------------------------%
%
% Author: Julien Fischer <jfischer@opturion.com>
% Modified by Alexander Schiendorfer for MiniBrass evaluation, 2016
%
% On-call Rostering
% ----------------
%
% Given a pool of N staff, we need to assign one staff member to each "day" in
% the rostering period.  Here a "day", means either one of Monday through to
% Thursday or all of Friday through Sunday.
% The latter is considered to be the weekend.
%
% Individual staff members may be unavailable on some days and may also be
% required to be on-call on some (other) days.
%
% Staff members should ideally work the same number of week days and the same
% number of weekends over the rostering period (and subject to the work load
% parameter).
% 
% Staff members should not be on-call for more than two days in a row (unless
% fixed in advance) and prefer not be on-call consecutive days in a row. 
%
% Staff members who are on-call over the weekend prefer not be on-call on
% the Wednesday before that weekend.
%
%----------------------------------------------------------------------------%

include "count.mzn";

% -- MiniBrass ----------------------------------------------------------------
include "evaluation-config.mzn";
include "soft_constraints/soft_constraints.mzn";
include "soft_constraints/cr_consistency.mzn";

% include PVS-specific libraries (always a constraint relationship) 
include "evaluation-pvs.mzn";

array[int, 1..2] of int: crCustomEdges;
array[int] of int: crCustomEdgesFlat = [ crCustomEdges[r,c] | r in index_set_1of2(crCustomEdges), c in 1..2] ;
array[int] of int: crossEdges = [if k = 1 then i else j endif | i in 1..custPref, j in custPref+1 .. nScs, k in 1..2 ];
array[int] of int: joined = crCustomEdgesFlat ++ crossEdges;
int: nCrRawEdges = max(index_set(joined)) div 2 ;


array[int, 1..2] of int: crTempEdges = array2d(1..nCrRawEdges, 1..2, joined);
crEdges = crTempEdges; %transClosure(SOFTCONSTRAINTS, crTempEdges); left out for performance reasons 
nCrEdges = max(index_set_1of2(crEdges));


% soft constraints:
% 1 for each day where we schedule the same guy twice
% 1 for each wednesday stuff 

int: lastPrefWorkDay = max(index_set(staffWork)); 
int: lastPrefNotWorkDay = max(index_set(staffNotWork)); 
int: custPref = lastPrefWorkDay + lastPrefNotWorkDay;
int: custAdjPref = custPref+lastIntAdjDay; 
nScs = custAdjPref + lastIntWeekendDay;

% scs structure .. .
% 1 .. lastPrefWorkDay                                      : preferences to work on a day
% lastPrefWorkDay + 1 .. custPref                           : preferences not to work on a day
% custPref + 1 .. custPref+lastIntAdjDay                    : preferences for adjacent days
% custAdjPref + 1 .. custAdjPref+lastIntWeekendDay          : preferences for wednesdays

% from lastIntAdjDay + lastIntWeekendDay + 1 till nScs 
% we write preferred work days for each surgeon


%----------------------------------------------------------------------------%

   % The number of staff.
   %
int: num_staff;
set of int: STAFF = 1..num_staff;

   % The required work load of each staff member expressed as a percentage
   % of full time.
   %
array[STAFF] of 1..100: work_load;

   % The number of days over which we are roster.
   % For the purposes of this roster Friday, Saturday and Sunday count as a
   % single day.
   %
int: num_days;
set of int: DAYS = 1..num_days;

    % This offset describes the first day beginning from day one of the
    % roster that is a weekend.  Subsequent weekends occur every five
    % days after that day.
    %
0..4: weekend_offset;

    % Set of days that each staff member is unavailable.
    %
array[STAFF] of set of int: unavailable;

    % The set of days that each staff member must be roster on.
    %
array[STAFF] of set of int: fixed;

   % What days in the roster are weekends?
   %
set of int: weekend_days = 
     % XXX surely there must be a simpler formula than this?
     { 
	d * 5 + weekend_offset + 1 | 
	d in 0 .. num_days div 5  
	     where d * 5 + weekend_offset + 1 <= num_days
     };

   % What days in the roster are week days?
   %
set of int: week_days =  DAYS diff weekend_days;

%-----------------------------------------------------------------------------%
 
var 0..max_week_day_bt: week_day_bt;
var 0..max_weekend_bt: weekend_bt;

par 0..num_days: max_week_day_bt; % found by previous runs with the original model
par 0..num_days: max_weekend_bt; % found by previous runs with the original model

%-----------------------------------------------------------------------------%
%
% Soft constraint violation penalties.
%

%int: lastPrefWorkDay = 1; 
%int: lastPrefNotWorkDay = 1; 

array[int] of STAFF: staffWork;
array[int] of set of DAYS: desiredWork;
array[int] of SOFTCONSTRAINTS: scWork;



constraint forall(i in 1..lastPrefWorkDay) (
  satisfied[scWork[i]] <-> (exists(d in desiredWork[i] intersect DAYS ) ( roster[d] = staffWork[i] ) )
);


array[int] of STAFF: staffNotWork;
array[int] of set of DAYS: desiredNotWork;
array[int] of SOFTCONSTRAINTS: scNotWork;


constraint forall(i in 1..lastPrefNotWorkDay) (
  satisfied[scNotWork[i]] <-> (forall(d in desiredNotWork[i] intersect DAYS ) ( roster[d] != staffNotWork[i] ) )
);


% The relative strengths of the "adjacent days" and "wednesday before weekend"
% constraints.  By default, these should be 1.  A constraint can be made stronger
% by increasing its value relative to the other one.
% Setting the value to 0 effectively disables the constraint.
%
int: adj_days_str;
int: wed_before_weekend_str;

%-----------------------------------------------------------------------------%

    % What staff member is rostered on-call for a particular day?
    %
array[1..num_days] of var STAFF: roster;

list of var STAFF: roster_week_days =
    [roster[i] | i in 1..num_days where i in week_days];

list of var STAFF: roster_weekend_days = 
    [roster[i] | i in 1..num_days where i in weekend_days];

    % How many week days is each staff member rostered on-call?
    %
array[STAFF] of var 0..num_days: week_days_oc;

    % How many weekends is each staff member rostered on-call?
    %
array[STAFF] of var 0..num_days: weekend_days_oc;

%-----------------------------------------------------------------------------%

% The constraints in this model are not satisfiable with less than two
% staff.
%
constraint assert(num_staff >= 2, "LESS THAN TWO STAFF");

constraint assert(num_days > 5, "LESS THAN ONE WEEK");

% Ensure that there are no days on which no staff are available.
%
set of int: all_unavailable = array_intersect(unavailable);

constraint assert(all_unavailable = {},
    "NO STAFF AVAILABLE ON DAYS: " ++ show(all_unavailable));

% Ensure that if there are fixed days on-call for a staff member that these
% days are not also ones on which that staff member is unavailable.
%
constraint forall(S in STAFF) (
   let {
       set of int: U = unavailable[S] intersect fixed[S]
   } in (
       assert(unavailable[S] intersect fixed[S] = {},
       "FIXED DAY CONFLICTS WITH UNAVAILABLE FOR STAFF MEMBER " ++ 
       show(S) ++ " ON DAYS " ++ show(U))
   )
);

% Ensure that only one surgeon is fixed to be on-call for a particular day.
%
constraint forall(i in 1..num_staff, j in i + 1 .. num_staff) (
    let {
	set of int: U = fixed[i] intersect fixed[j]
    } in (
    assert(fixed[i] intersect fixed[j] = {},
	"FIXED DAYS FOR SURGEON " ++ show(i) ++ " AND SURGEON " ++
	show(j) ++ " CONFLICT ON DAYS " ++ show(U))
    )
);

%-----------------------------------------------------------------------------%

% Associate week days in the roster with week days rostered on-call for each
% surgeon.  Associate weekend days in the roster with weekend days rostered
% on-call for each surgeon.
%
constraint forall(S in STAFF) (
   count(roster_week_days, S, week_days_oc[S]) /\
   count(roster_weekend_days, S, weekend_days_oc[S])
);

constraint forall(i in 1..num_staff, j in i + 1 .. num_staff) (
    abs(work_load[i] * week_days_oc[j] - work_load[j] * week_days_oc[i]) <= 100 * week_day_bt /\
    abs(work_load[i] * weekend_days_oc[j] - work_load[j] * weekend_days_oc[i]) <= 100 * weekend_bt
);    

%-----------------------------------------------------------------------------%

% Take any fixed roster allocations into account.
%
constraint forall (s in STAFF, D in fixed[s]) (
    roster[D] = s
);

%-----------------------------------------------------------------------------%

set of int: all_fixed = array_union(fixed);

% Disallow the same staff member being on-call three days in a row *unless*
% those three days were all fixed by the input.
%
constraint forall (i in 1..num_days - 2) (
    if
	i in all_fixed /\
	i + 1 in all_fixed /\
	i + 2 in all_fixed
    then
	true
    else
        roster[i] = roster[i + 1] -> roster[i + 2] != roster[i]
    endif
);

%-----------------------------------------------------------------------------%

% Minimize the number of times a staff member is roster on-call for
% consecutive days.
%

% scs custPref+1..custPref+lastIntAdjDay are reserved for the adjacent day problem 
int: lastIntAdjDay = num_days - 1;

constraint forall (i in 1.. num_days - 2) (
    roster[i] != roster[i + 1] <-> satisfied[custPref+i]
);

%-----------------------------------------------------------------------------%

% Staff member cannot be rosterd on-call when they are unavailable.
%
%
%constraint forall (S in STAFF, i in 1..num_days) (
%     if i in unavailable[S] then roster[i] != S else true endif
%);

% reformulated for readability
constraint forall (s in STAFF, i in unavailable[s] intersect DAYS) (
     roster[i] != s 
);
%-----------------------------------------------------------------------------%

% If a staff member is on-call on a Thursdays or a Monday then they cannot
% be on-call over the weekend *unless* the this has been fixed in the input.
%
constraint forall (i in 1..num_days) (
    i in weekend_days -> (
      % We need to treat the end-points of the roster specially here.
      if i = 1 then
        if i in all_fixed /\ i + 1 in all_fixed then
	    true
	else
	    roster[i] != roster[i + 1]
        endif 
      elseif i = num_days then
	if i in all_fixed /\ i - 1 in all_fixed then
	    true
        else
            roster[i] != roster[i - 1]
        endif 
      else
         if i in all_fixed /\ i + 1 in all_fixed /\ i - 1 in all_fixed then
	    true 
	 else
             roster[i] != roster[i + 1] /\ roster[i] != roster[i - 1]
         endif
      endif
    )
);

%-----------------------------------------------------------------------------%

% If a staff member is on-call over the weekend, then we prefer them not be
% on-call on the previous Wednesday.
%

% scs custAdjPref+1..custAdjPref + lastIntWeekendDay are reserved for the adjacent day problem 
int: lastIntWeekendDay = card(weekend_days);


constraint forall (i in 1..card(weekend_days) where weekend_days[i] > 2) (

    roster[weekend_days[i]] != roster[weekend_days[i] - 2] <-> 
        satisfied[custAdjPref+i]
);

%-----------------------------------------------------------------------------%

% Staff member cannot be on-call on consecutive weekend *unless* this was
% fixed in the input.
%
constraint forall(i in 1..card(weekend_days) - 1) (
    if weekend_days[i] in all_fixed /\ weekend_days[i + 1] in all_fixed
    then true
    else roster[weekend_days[i]] != roster[weekend_days[i + 1]]
    endif
);


%-----------------------------------------------------------------------------%

ann: custSearch = seq_search([
        int_search(roster, anti_first_fail, indomain_split, complete),
        int_search(roster_week_days, anti_first_fail, indomain_split, complete),
        int_search(roster_weekend_days, anti_first_fail, indomain_split, complete),
        int_search(week_days_oc, input_order, indomain_split, complete),
	      int_search(weekend_days_oc, input_order, indomain_split, complete),
        int_search([week_day_bt, weekend_bt], input_order, indomain_split, complete)
    ]);

array[int] of var int: lnsVars = roster;

include "evaluation-heuristics.mzn";

include "evaluation-search.mzn";


%-----------------------------------------------------------------------------%

output [
    "roster     = ", show(roster), ";\n",
    "weekday bt = ", show(week_day_bt), ";\n",
    "weekend bt = ", show(weekend_bt), ";\n",
    "penSum = ", show(penSum), "\n",
    "violatedScs = \(violatedScs)\n",
    "weekend_days = \(weekend_days);\n", 
    "nScs = \(nScs);\n",
    "nCrEdges = \(nCrEdges );\n",
];

%-----------------------------------------------------------------------------%
%-----------------------------------------------------------------------------%

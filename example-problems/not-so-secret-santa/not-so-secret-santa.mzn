include "globals.mzn";

/* the actual decision variables and *hard* constraints */

int: p; % number of people
set of int: PEOPLE = 1..p;
array[PEOPLE] of var PEOPLE: givesTo;
array[PEOPLE] of var PEOPLE: getsFrom;

% we can add tags to people to find matching interests 
int: t;
set of int: TAGS = 1..t;

% we include this instance to be able to define variables
include "small-example.mzn";

array[PEOPLE] of set of TAGS: tagged;

% we can exclude specific people from our candidates (spouses that would get a gift anyway)
array[PEOPLE] of set of PEOPLE: excludes;


/* everybody is clearly assigned to another person */
constraint inverse(givesTo, getsFrom);

/* people do not give to themselves */
constraint forall(p_ in PEOPLE) (givesTo[p_] != p_);

solve 
%:: int_search(givesTo, input_order, indomain_random, complete)
satisfy;
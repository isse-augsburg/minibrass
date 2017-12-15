---
layout: post
title:  "Not so secret santa"
author: Alexander Schiendorfer
---
When there holiday season is reaching its peak, wishes are everywhere (so of course also in Augsburg, see below :) ),
we provide you with a MiniBrass model to calculate a "not-so-secret" Santa (involving preferences). 

We can have preferences regarding our assigned secret santa (we want somebody who'll give us food or something handmade ...) and the person we want to give something, too.

![augsburg]({{ "/img/christkindl.jpg" | prepend: site.baseurl }} "Augsburger Christkindlesmarkt") 
<https://www.flickr.com/photos/augschburger/72455954/>

### Our constraint model 
The essential decisions involve the assigned secret santa (and vice versa to express preferences the other way around later)
```c++
include "not-so-secret-santa_o.mzn"; /* the compiled mzn-output */
include "soft_constraints/minibrass.mzn";  /* MiniBrass prepared searches */
include "globals.mzn";

/* the actual decision variables and *hard* constraints */

int: p; % number of people
set of int: PEOPLE = 1..p;
array[PEOPLE] of var PEOPLE: givesTo;
array[PEOPLE] of var PEOPLE: getsFrom;
```
In addition, we want to add tags to the people involved (for interests such as I want to give/get something handmade, food or something active).
```c++
% we can add tags to people to find matching interests 
int: t;
set of int: TAGS = 1..t;

% we include this instance to be able to define variables
include "small-example.mzn";
array[PEOPLE] of set of TAGS: tagged;
```
and some of the pairings should be forbidden (e.g. couples that give each other anyway)
```c++ 
% we can exclude specific people from our candidates (spouses that would get a gift anyway)
array[PEOPLE] of set of PEOPLE: excludes;
```
the only hard constraints is that everyone is assigned a different person (subsumed by the `inverse` constraint)
and noone is assigned to themselves
```c++ 
/* everybody is clearly assigned to another person */
constraint inverse(givesTo, getsFrom);

/* people do not give to themselves */
constraint forall(p in PEOPLE) (givesTo[p] != p);

solve search miniBrass();
```
We could add some more randomness by specifying
```c++ 
solve 
:: int_search(givesTo, input_order, indomain_random, complete)
search miniBrass();
```
### Our preference model 
We now consider a particular instance (found [here](https://github.com/isse-augsburg/minibrass/blob/master/example-problems/not-so-secret-santa/small-example.mzn)) 
where August, Hans, Hermann, Maria, and Franziska decide on their not-so-secret santa (their preferences are only
given to an independent judge -- say, a grandmother):

First, we start with August:
```c++
/* let's get all available types */
include "defs.mbr";

PVS: august = new ConstraintPreferences("august") {
  soft-constraint augustToMaria : 'givesTo[AUGUST] = MARIA';
  soft-constraint augustFood : 'FOOD in tagged[givesTo[AUGUST]]';
  soft-constraint augustClumsy : 'not(HOMEMADE in tagged[givesTo[AUGUST]])';
  
  crEdges : '[| mbr.augustClumsy, mbr.augustToMaria | 
                mbr.augustFood, mbr.augustToMaria |]';
  useSPD: 'false' ;
};
```
August secretly wants to give a present to Maria, wants to give a present to a food-lover, but rather not to a
person that expects something handmade (he is rather clumsy).
Giving to Maria is more important than the other two soft-constraints which is reflected
by the edges `mbr.augustClumsy, mbr.augustToMaria` and  `mbr.augustFood, mbr.augustToMaria`. If Maria does
not like food, we cannot satisfy both `augustFood` and `augustToMaria` - so it's useful 
to have an edge indicating priority.

Hans is more quantitatively oriented and wants to assign weights to his soft constraints 
(he'd rather give food to someone and does not want to give to Hermann, his notoriously hard to satisfy brother)

```c++
PVS: hans = new WeightedCsp("hans") {
  soft-constraint hansFood : 'FOOD in tagged[givesTo[HANS]]' :: weights('10');
  soft-constraint hansHermannBrothers : 
     'not(givesTo[HANS] = HERMANN)' :: weights('5');
};
```
Hermann, on the other, does not care about weights or preference relations, he just wants as many soft constraints
satisfied as possible (best if he can make a homemade gift suitable for active people):

```c++
PVS: hermann = new MaxCsp("hermann") {
  soft-constraint hermannActive : 'ACTIVE in tagged[givesTo[HERMANN]]';
  soft-constraint hermannHomemade : 'HOMEMADE in tagged[givesTo[HERMANN]]';
};
```

Finally, Maria and Franziska specify their preferences: 
```c++
PVS: maria = new ConstraintPreferences("maria") {
  soft-constraint mariaToAugust : 'givesTo[MARIA] = AUGUST';
  soft-constraint mariaCoupon : 'COUPON in tagged[givesTo[MARIA]]';
  soft-constraint mariaHandy : 'HOMEMADE in tagged[givesTo[MARIA]]';
  
  crEdges : '[| mbr.mariaToAugust, mbr.mariaHandy | 
                mbr.mariaToAugust, mbr.mariaCoupon |]';
  useSPD: 'false' ;
};

PVS: franziska = new MaxCsp("franziska") {
  soft-constraint franziskaGetsHomeMade : 
          'HOMEMADE in tagged[getsFrom[FRANZISKA]]';
  soft-constraint franziskaGivesHomemade : 
          'HOMEMADE in tagged[givesTo[FRANZISKA]]';
};

output '["givesTo = \(givesTo);"]' ;
```

Since everyone is using a different scale, we apply [Condorcet voting](https://en.wikipedia.org/wiki/Condorcet_method) to get our result.
```c++
solve vote([august,hans,hermann,maria,franziska], condorcet);
```

### The output
When we compile the preference model, we see a stream of solutions where each subsequent solution is preferred by a majority of voters.

```
Intermediate solution:givesTo = [5, 1, 4, 3, 2];
Valuations: mbr_overall_august = 1..1; mbr_overall_hans = 10; 
            mbr_overall_hermann = 1; mbr_overall_maria = 1..3; 
            mbr_overall_franziska = 0
----------
Intermediate solution:givesTo = [4, 1, 5, 2, 3];
Valuations: mbr_overall_august = 2..2; mbr_overall_hans = 10; 
            mbr_overall_hermann = 1; mbr_overall_maria = 1..1; 
            mbr_overall_franziska = 2
----------
Intermediate solution:givesTo = [3, 5, 4, 2, 1];
Valuations: mbr_overall_august = 1..2; mbr_overall_hans = 0; 
            mbr_overall_hermann = 1; mbr_overall_maria = 1..1; 
            mbr_overall_franziska = 0
----------
Intermediate solution:givesTo = [4, 5, 1, 3, 2];
Valuations: mbr_overall_august = 2..2; mbr_overall_hans = 0; 
            mbr_overall_hermann = 0; mbr_overall_maria = 1..3; 
            mbr_overall_franziska = 0
----------
==========
```
For instance, if we compare the last two solutions `[3, 5, 4, 2, 1]` and `[4, 5, 1, 3, 2]`, August gets one more constraint satisfied, Hans and Franziska are equally satisfied,
Hermann is happier but Maria gets two more soft constraints violated. Maria, unfortunatley, has to take one for the team here.

Interestingly, `[4, 5, 1, 3, 2]` is also the solution that maximizes the number of voters getting their top priority:

```c++
solve vote([august,hans,hermann,maria,franziska], majorityTops);
```

Written out, this means: 

```
August gives to Maria    (his most important constraint)
Hans gives to Franziska  (a person that likes food and is not Hermann)
Hermann gives to August  (an active, homemade-style person)
Maria gives to Hermann   (unfortunately, no wish of hers was respected)
Franziska gives to Hans  (both her secret santa and Hans like homemade stuff)
```

With exception of Maria, all other people are pretty happy (and we hope Maria gets at least a really nice present from August).

Season's greetings!

The full model can be found at <https://github.com/isse-augsburg/minibrass/tree/master/example-problems/not-so-secret-santa>

## Actually random secret santa

If you want to learn how to *actually* assign people randomly to their secret santas without any preferences (which we certainly do not recommend), have a look at this video:

<iframe width="560" height="315" src="https://www.youtube.com/embed/5kC5k5QBqcc" frameborder="0" gesture="media" allow="encrypted-media" allowfullscreen></iframe>

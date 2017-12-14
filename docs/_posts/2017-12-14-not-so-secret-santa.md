---
layout: post
title:  "Not so secret santa"
author: Alexander Schiendorfer
---
Since holiday season is reaching its peak (so of course also in Augsburg :) ), we thought it would be a nice opportunity to show MiniBrass calculating a "not-so-secret" Santa. We can have preferences regarding our assigned secret santa (we want somebody who'll give us food or something handmade ...) and the person we want to give something, too.

![augsburg]({{ "/img/christkindl.jpg" | prepend: site.baseurl }} "Augsburger Christkindlesmarkt") 
<https://www.flickr.com/photos/augschburger/72455954/>

Our constraint model
```c++
include "not-so-secret-santa_o.mzn"; /* the compiled mzn-output */
include "soft_constraints/minibrass.mzn";  /* MiniBrass prepared searches */
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
constraint forall(p in PEOPLE) (givesTo[p] != p);

solve search miniBrass();

```

Our preference model 
```c++
include "defs.mbr";

PVS: august = new ConstraintPreferences("august") {
  soft-constraint augustToMaria : 'givesTo[AUGUST] = MARIA';
  soft-constraint augustFood : 'FOOD in tagged[givesTo[AUGUST]]';
  soft-constraint augustClumsy : 'not(HOMEMADE in tagged[givesTo[AUGUST]])';
  
  crEdges : '[| mbr.augustClumsy, mbr.augustToMaria | mbr.augustFood, mbr.augustToMaria |]';
  useSPD: 'false' ;
};

PVS: hans = new WeightedCsp("hans") {
  soft-constraint hansFood : 'FOOD in tagged[givesTo[HANS]]' :: weights('10');
  soft-constraint hansHermannBrothers : 'not(givesTo[HANS] = HERMANN)' :: weights('5');
};

PVS: hermann = new MaxCsp("hermann") {
  soft-constraint hermannActive : 'ACTIVE in tagged[givesTo[HERMANN]]';
  soft-constraint hermannHomemade : 'HOMEMADE in tagged[givesTo[HERMANN]]';
};

PVS: maria = new ConstraintPreferences("maria") {
  soft-constraint mariaToAugust : 'givesTo[MARIA] = AUGUST';
  soft-constraint mariaCoupon : 'COUPON in tagged[givesTo[MARIA]]';
  soft-constraint mariaHandy : 'HOMEMADE in tagged[givesTo[MARIA]]';
  
  crEdges : '[| mbr.mariaToAugust, mbr.mariaHandy | mbr.mariaToAugust, mbr.mariaCoupon |]';
  useSPD: 'false' ;
};

PVS: franziska = new MaxCsp("franziska") {
  soft-constraint franziskaGetsHomeMade : 'HOMEMADE in tagged[getsFrom[FRANZISKA]]';
  soft-constraint franziskaGivesHomemade : 'HOMEMADE in tagged[givesTo[FRANZISKA]]';
};

output '["givesTo = \(givesTo);"]' ;

solve vote([august,hans,hermann,maria,franziska], condorcet);
```

The full model can be found at <https://github.com/isse-augsburg/minibrass/tree/master/example-problems/not-so-secret-santa>

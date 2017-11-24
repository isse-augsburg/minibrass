---
title: Welcome to MiniBrass
permalink: /docs/home/
redirect_from: /docs/index.html
---

## Getting started


Many combinatorial optimization problems are conveniently expressed using a constraint-based modeling language such as [MiniZinc]. Then they are solved by powerful constraint programming or mathematical programming solvers.
MiniBrass targets **over-constrained problems** or problems where desirable properties are modeled as optional (soft) constraints. 
It is a *language* and third-party *MiniZinc library* that adds support for MiniZinc/MiniSearch to model **weak/soft constraints** and preferences systematically. 
Depending on the problem at hand, importance is expressed in different types.

Consider this figure to get a quick idea:

![alt text]({{site.baseurl}}/img/codeHelloWorld.png "Logo Title Text 1")

MiniBrass pays tribute to the tradition of naming NICTA's G12 software after elements in the 12th group of the periodic table. Brass is firstly an alloy that contains zinc and, according to the [German Wikipedia](https://de.wikipedia.org/wiki/Messing), 
> Cold forming is possible with brass alloys containing up to 37% zinc. At higher zinc rates, only warm forming at temperatures > 600 °C is possible.


Our library extensions are built on top of [MiniZinc](http://www.minizinc.org) that facilitates modeling combinatorial optimization problems for various solvers. The library includes:

* Separation of constraints and preferences in different files
* A graphical notation to communicate preference specifications with clients or end-users
* Automatic conversion between compatible preference types (e.g., set-based maximal satisfaction to weighted CSP)
* Generic search heuristics based on the goal type
* Automated consistency checks of the specified user input
* Preference aggregation by voting strategies to make *socially fair choices* 

## Quick Example

First, we define variables, domains, and conventional hard constraints

**1.** Create a new constraint model

```c++
include "classic_o.mzn"; % output of minibrass
include "soft_constraints/minibrass.mzn"; 

% the basic, "classic" CSP 
set of int: DOM = 1..3;

var DOM: x; var DOM: y; var DOM: z;

solve 
:: int_search([x,y,z], input_order, indomain_min, complete)
search miniBrass();
```

**2.** Create a new preference model

```c++
include "defs.mbr";
  
PVS: cr1 = new ConstraintPreferences("cr1") {
   soft-constraint c1: 'x + 1 = y';
   soft-constraint c2: 'z = y + 2';
   soft-constraint c3: 'x + y <= 3';
   
   crEdges : '[| mbr.c2, mbr.c1 | mbr.c3, mbr.c1 |]';
   useSPD: 'false' ;
}; 

output '["x = \(x); y = \(y); z = \(z)"]';

solve cr1;
```

### Tech

MiniBrass is based on these open source projects:

* [MiniZinc] - Of course, its underlying modeling language!
* [MiniSearch] - an extension of MiniZinc to support more flexible search strategies


   [MiniZinc]: <http://www.minizinc.org/>
   [MiniSearch]: <http://www.minizinc.org/minisearch/>

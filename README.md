# MiniBrass

MiniBrass is a library that adds support for soft constraints to MiniZinc/MiniSearch to model weak/soft constraints and preferences systematically. 
MiniBrass pays tribute to the tradition of naming NICTA's G12 software after elements in the 12th group of the periodic table. Brass is firstly an alloy that contains zinc and, according to the [German Wikipedia](https://de.wikipedia.org/wiki/Messing), 
> Cold forming is possible with brass alloys containing up to 37% zinc. At higher zinc rates, only warm forming at temperatures > 600 °C is possible.

More specifically, we aim to provide
- Convenient expression of soft constraint problems using constraint relationships
- Automatic translation to weighted CSP
- Generic search heuristics
- Consistency checks of the specified relationships
- Integration of soft global constraints

For a *user-centered* perspective, please refer to our [main page](http://isse-augsburg.github.io/constraint-relationships/).

### Tech

MiniBrass is based on these open source projects:

* [MiniZinc] - Of course, its underlying modeling language!
* [MiniSearch] - an extension of MiniZinc to support more flexible search strategies

### Project overview
* **source-code/minizinc** contains the library itself
* **source-code/java** provides our preliminary compiler for MiniBrass PVS extensions
* **source-code/kiv** contains PVS-related proofs in the KIV theorem prover
* **example-problems** shows problems referred to in the documentation

### Todos

 - Add support for soft-GCC 
 - Implement 

   [MiniZinc]: <http://www.minizinc.org/>
   [MiniSearch]: <http://www.minizinc.org/minisearch/>
 


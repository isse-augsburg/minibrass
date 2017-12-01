---
title: PVS-Types
permalink: /docs/pvstypes/
---

MiniBrass revolves around the central concept of *partial valuation structures* (also called *preference valuation structures*, PVS).
A PVS instance represents a coherent set of soft constraints (or preferences) that all map a solution to the *element type* of the underlying PVS-type.
The element type must also offer a (partial) ordering and a *combination operation* to aggregate several individual valuations.

But let's first consider an example implemented in the MiniBrass standard library.  
 
*Weighted constraints* assign a weight `w[c]` to every soft constraint `c` that acts as a penalty if violated. The overall violation is found
by *summing up*  all penalties.

To see it in action, consider the following example
```c++
include "defs.mbr";

PVS: user1Prefs = new WeightedCsp("user1Prefs") {
  soft-constraint c1: 'x > y' :: weights('3');
  soft-constraint c2: 'x = y + 2' :: weights('1');
};

output '["x = \(x), y = \(y)"]';

solve user1Prefs;
```
based on a simplistic constraint model
```c++
include "soft_constraints/minibrass.mzn";
include "pvsTypes_o.mzn";

var 0..3: x;
var 0..3: y;

solve search miniBrass();
``` 
It can be interpreted as "it's pivotal that `x` is greater than `y`" and it would be even better if the difference were exactly 2.

Our output shows the results to this model
```
Intermediate solution:x = 0, y = 0
Valuations: mbr_overall_user1Prefs = 4
----------
Intermediate solution:x = 2, y = 0
Valuations: mbr_overall_user1Prefs = 0
----------
==========
``` 
The first found solution (`x = 0, y = 0`) violates both `x > y` with penalty 3 and `x = y + 2` with penalty 1. Hence the score 4.

The second solution (`x = 2, y = 0') satisfies both soft constraints, thus resulting an overall score of 0. 

But how is the type `WeightedCsp` actually defined?
## Inspecting an existing type
To see this, we take a look at its definition in `defs.mbr`, found in the `mbr_std` folder in the `mbr2mzn.jar` directory (see [here](https://github.com/isse-augsburg/minibrass/blob/master/source-code/java/mbr_std/defs.mbr)). 

```c++
type WeightedCsp = PVSType<bool, int> = 
  params {
    int: k :: default('1000'); 
    array[1..nScs] of int: weights :: default('1');
  } in 
  instantiates with "soft_constraints/mbr_types/weighted_type.mzn" {
    times -> weighted_sum;
    is_worse -> is_worse_weighted;
    top -> 0;
 }
 offers {
    heuristics -> getSearchHeuristicWeighted;
 };
```
`WeightedCsp` shows all important characteristics :

* An *element type* (the scale, weight, etc. by which we want to order solutions) which here corresponds to `int`
* A *specification type* which refers to the data type of the soft constraint expressions. Most often, this is `bool` but sometimes we want something like a cost function that directly maps to `int`
* A combination operation `weighted_sum`, which means that we build a some of all violated soft constraints, weighted by their importance
* An ordering relation `is_worse_weighted` which orders the elements of the element type and a top element (here 0)

The actual implementations of `weighted_sum` and `is_worse_weighted` are a MiniZinc function and a MiniZinc predicate, respectively, which are defined in `mbr_types/weighted_type.mzn` (see [here](https://github.com/isse-augsburg/minibrass/blob/master/source-code/minizinc/mbr_types/weighted_type.mzn)).

```c++
function var int: weighted_sum(array[int] of var bool: b, 
                               par int: nScs, par int: k, array[int] of par int: weights) =
  sum(i in index_set(b)) ( (1 - bool2int(b[i])) * weights[i]);

predicate is_worse_weighted(var int: x, var int: y, 
  par int: nScs, par int: k, array[int] of par int: weights) = 
  x > y;
```
These ingredients are all encoded in the PVS type and thus reusable for, e.g., many `WeightedCsp` instances but also compatible with other PVS types.

## Common PVS types
The following table highlights the commonalities of PVS types. By convention, we always read a predicate `a is_worse_than b` from left to right.
Hence, for weighted CSP, `a is_worse_than b` if and only if `a > b` (since there is a higher violation).

<table class="table table-striped table-hover ">
  <thead>
    <tr>
      <th>PVS-Type</th>
      <th>Spec. / Element type</th>
      <th>times</th>
      <th>is_worse</th>
      <th>top</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Weighted CSP</td>
      <td>bool / int</td>
      <td>weighted_sum</td>
      <td>is_greater_than</td>
      <td>k (max violation)</td>
    </tr>
     <tr>
      <td>Cost Function Networks</td>
      <td>int</td>
      <td>sum</td>
      <td>is_greater_than</td>
      <td>k</td>
    </tr>
    <tr>
      <td>Set-Based Max CSP</td>
      <td>bool / set of int</td>
      <td>set_union</td>
      <td>superset</td>
      <td>{}</td>
    </tr>
    <tr>
      <td>Fuzzy CSP</td>
      <td>[0.0 1.0]</td>
      <td>min</td>
      <td>is_less_than</td>
      <td>1.0</td>
    </tr>
  </tbody>
</table> 

All standard types can be found [here](https://github.com/isse-augsburg/minibrass/blob/master/source-code/java/mbr_std/defs.mbr).



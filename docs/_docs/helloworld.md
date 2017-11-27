---
title: Hello World
permalink: /docs/helloworld/
---
## The problem
Consider the following (simplified) scenario: We have to create a roster composed of three nurses.
Each nurse can be assigned to night or day shift, or have the day off. In terms of strict constraints,
at least one person needs to take the night shift (by regulation).

Regarding soft or desirable constraints, we know from experience that
it is *preferable* to have two people take the night shift (`equality`). Moreover one nurse asked us to get the day off and
one prefers not to be on night shift but day is fine (`noNights`). Although, we aim at considering all involved people, the overall organizational 
constraint `equality` is deemed more important than the individual wishes. Since we are aiming at a democratic setting,
satisfying nurse two's wish is **not** comparable to nurse three's wish. Graphically, we can depict this as follows:

![alt text]({{site.baseurl}}/img/nursePrefs.png "A first example")

## Modeling 

### MiniZinc Model

### MiniBrass Model

### Inspecting the results

## Source code

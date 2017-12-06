These scripts verify that no intended tuning took place for the MiniBrass evaluation.

Background
========== 
In the originally submitted benchmark files for soft-queens, there was a line stating
"
% Chuffed (with --free) seems to perform better without :: domain
% on the first constraint.
constraint all_different(queens); % :: domain;
"

This line was actually added by the original author of the (hard constraint) model, Hakan Kjellerstrand. Understandably, this led to confusion in the initial reviews (why are we pushing Chuffed?)

First of all, we never used the solver "Chuffed" in any of our evaluations.

Second, to provide evidence that these settings did not influence the solvers' performance at all, with the ":: domain" annotation being activated (it was commented out before). 

We observed negligible changes in the performance and document the results in this folder: 

- new-data.csv ... The runtimes with ":: domain" activated
- old-data.csv ... The runtimes with ":: domain" commented out
- compare.db   ... A database containing both tables
- statTest.ipynb ... An ipython notebook offering a statistical test
- analyseDomainAnnotAggregated.sql
               ... An SQL script calculating the difference (if any) between both settings:

```
--------------------------------------
| Times solved now but not before | Times solved optimally now but not before | Avg Runtime Difference (secs) |
|               0                 |                     0                     |          -0.0012

``` 

The very little time difference is due to measurement errors but no systematic advantage or disadvantage could be found.
Also, a statistical test revealed no significant differences (t= -0.000134,  prob =  0.9998, see statTest.ipynb).

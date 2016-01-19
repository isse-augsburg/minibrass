include "minisearch.mzn";
var 0..10: x;
var 0..8: y; 
var 0..18: obj;

constraint obj = x + y;

constraint x < y;

output["x = \(x), y = \(y), obj = \(obj)"];

solve 

search lns_max(obj, [x,y], 5, 0.5) 
        /\ if hasSol() then print() 
           else print("No solution found\n") 
       endif;


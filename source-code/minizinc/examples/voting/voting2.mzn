% voting 2
include "minisearch.mzn";

function ann: postGetBetter() = 
  post(
      bool2int(betterForA(choice, sol(choice)))
      + bool2int(betterForB(choice, sol(choice)))
      + bool2int(betterForC(choice, sol(choice))) 
      > 1
  );
  
predicate betterForA(var OPTIONS: o1, par OPTIONS: o2) = 
let {
  constraint table([o1,o2], [| 3, 1 | 3, 2 | 1, 2 |] );
} in
(
 true
);

predicate betterForB(var OPTIONS: o1, var OPTIONS: o2) = 
let {
  constraint table([o1,o2], [| 2, 1 | 2, 3 | 1, 3 |] );
} in
(
  true
);

predicate betterForC(var OPTIONS: o1, var OPTIONS: o2) = 
let {
  constraint table([o1,o2], [| 3, 2 | 3, 1 | 2, 1 |] );
} in
(
  true
);



function ann: pvs_BAB() =
       repeat(
           if next() then 
                 print("Intermediate solution:") /\ print() /\
                 commit() /\ postGetBetter()
           else break endif
       );

set of int: OPTIONS = 1..3;

var OPTIONS: choice;
var OPTIONS: nextChoice; 

var bool: aLikesThat; 
var bool: bLikesThat;
var bool: cLikesThat;

array[1..3] of var bool: likes = [aLikesThat, bLikesThat, cLikesThat];

%constraint choice = 1;
%constraint nextChoice = 2;

%constraint aLikesThat = betterForA(nextChoice, choice);
constraint bLikesThat = betterForB(nextChoice, choice);
constraint cLikesThat = betterForC(nextChoice, choice);



solve search pvs_BAB();

output["choice = \(choice)"];
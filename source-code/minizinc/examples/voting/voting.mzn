set of int: OPTIONS = 1..3;

var OPTIONS: choice;
var OPTIONS: nextChoice; 

var bool: aLikesThat; 
var bool: bLikesThat;
var bool: cLikesThat;

array[1..3] of var bool: likes = [aLikesThat, bLikesThat, cLikesThat];

constraint choice = 1;
constraint nextChoice = 2;

constraint aLikesThat = betterForA(nextChoice, choice);
constraint bLikesThat = betterForB(nextChoice, choice);
constraint cLikesThat = betterForC(nextChoice, choice);

predicate betterForA(var OPTIONS: o1, var OPTIONS: o2) = 
(
  (o1 = 3 /\ o2 = 1) \/ (o1 = 3 /\ o2 = 2) \/ (o1 = 1 /\ o2 = 2)
);

predicate betterForB(var OPTIONS: o1, var OPTIONS: o2) = 
(
  (o1 = 2 /\ o2 = 1) \/ (o1 = 2 /\ o2 = 3) \/ (o1 = 1 /\ o2 = 3)
);

predicate betterForC(var OPTIONS: o1, var OPTIONS: o2) = 
(
  (o1 = 3 /\ o2 = 2) \/ (o1 = 3 /\ o2 = 1) \/ (o1 = 2 /\ o2 = 1)
);


solve satisfy;

set of int: range = 1..3;

predicate lessThan(var set of range: x, var set of range: y) = 
let {
  var set of range: dif;
  constraint dif != {} /\ dif subset y /\ dif intersect x = {};
} in
(
  x union dif = y
);

var set of range: x;
var set of range: y;

%constraint x subset y;
constraint lessThan(x,y);
constraint x != y;
constraint x != {};

solve satisfy;
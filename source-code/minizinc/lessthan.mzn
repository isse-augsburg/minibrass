% custom 'less than' "implementation" as predicate
predicate lessThan(var int: x, var int: y) = 
let {
  var int: dif;
  constraint dif > 0;
} in
(
  x + dif = y
);

var 0..2:x;
var 0..2:y;

constraint lessThan(x,y);

solve satisfy;

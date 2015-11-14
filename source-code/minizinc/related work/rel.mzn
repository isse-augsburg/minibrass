% matching predicate for two ints
% rel(x, y) <-> x = 2 * y 
predicate rel (var int: x, var int: y) = 
let {
  var int: z;
  constraint z = y;
}
in 
(
x = z+y);

function var bool: frel(var int: x, var int: y) :: promise_total = 
(
   if rel(x,y) then 
     true
   else 
     false
   endif
);

predicate relwrapped(var int: x, var int: y) = (
  frel(x,y) = true
);

set of int: dom = 1..5;
var dom: x;
var dom: y;

constraint relwrapped(x,y);

solve satisfy;
function var int: myabs(var int: x) :: promise_total = 
  let { var int: y;
        constraint int_abs(x,y) } in y;

var -5..5: x;
var -5..5: y;

constraint not(x = myabs(y));
constraint y = -3;

solve satisfy;
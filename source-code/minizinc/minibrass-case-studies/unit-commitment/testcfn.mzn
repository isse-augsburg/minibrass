function var int: sum(array[int] of var int: b, 
                               par int: nScs, par int: k, par bool: isWorstCase) =
  if(isWorstCase) then
    max(b)
  else 
    sum(b)
  endif;

array[1..3] of var int: xs = [2, 1, 3];
var int: m = sum(xs, 10, 15, true);
var int: s = sum(xs, 10, 15, false);
output["xs = \(xs), m = \(m), s = \(s)"];

solve satisfy;

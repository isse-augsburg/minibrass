var 0..2: x;
var 0..2: y;

0..2: upperBound;

constraint y < upperBound;

constraint x < y;

solve satisfy;
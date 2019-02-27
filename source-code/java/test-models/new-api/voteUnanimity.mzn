var 0..10: voteValA;
var 0..10: voteValB;  

include "table.mzn";

constraint table([voteValA, voteValB], [| 0, 8 | 2, 8 | 3, 9 | 5, 7 |]); 

solve
:: int_search([voteValA], input_order, indomain_min, complete )
  satisfy;
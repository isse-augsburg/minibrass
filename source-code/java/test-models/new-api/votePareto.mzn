var 0..10: voteVal;
var 0..10: objective;  

include "table.mzn";

constraint table([voteVal, objective], [| 0, 10 | 2, 8 | 3, 9 | 5, 7 |]); 

solve
:: int_search([voteVal], input_order, indomain_min, complete )
  satisfy;
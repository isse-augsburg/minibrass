int: n; 
int: m = n div 2;

% Utility functions
% ---------------------------------------------------
% isNextTo(person1, person2) makes a safe check whether
% person1 and person2 are next to each other 
% (left, right, up, down)
function var 0..1: isNextTo(PEOPLE: p1, PEOPLE: p2) = 
let {
  var 1..2: row = personToRow[p1];
  var 1..m: col = personToCol[p1];
} in bool2int(
    placementSafe(p2, row + 1, col) \/ placementSafe(p2, row, col + 1) \/
    placementSafe(p2, row - 1, col) \/ placementSafe(p2, row, col - 1)
);

function var bool: placementSafe(PEOPLE: p2, var int: i, var int: j) = 
          if i < 0 \/ i > 2 \/ j < 0 \/ j > m then false
          else  
            placement[i,j] = p2
          endif;
 

set of int: PEOPLE = 1..n;
array[PEOPLE, PEOPLE] of par bool: friend;

array[1..2, 1..m] of var PEOPLE: placement;
array[PEOPLE] of var 1..2: personToRow;
array[PEOPLE] of var 1..m: personToCol;

constraint forall (i in 1..2, j in 1..m) (  personToRow[placement[i,j]] = i );
constraint forall (i in 1..2, j in 1..m) (  personToCol[placement[i,j]] = j );
 
% wants to be in the second row 
% personToRow[1] = 2 

include "alldifferent.mzn";

constraint alldifferent([placement[i,j] | i in 1..2, j in 1..m]);

solve satisfy;

output [ "Row 2: "] ++ [ "\(placement[2,j]) " | j in 1..m ] ++ ["\n"] ++ 
       [ "Row 1: "] ++ [ "\(placement[1,j]) " | j in 1..m ]; 
% =============================================
% Utility library for encoding fuzzy CSPs in
% MiniZinc; see example usage below
%
% Takes care of posting appropriate
% table constraints from cost vectors
% ---------------------------------------------

include "table.mzn";

function var float: fbinary_fuzzy_rational(array[int] of int: values, int: denominator, var int: x, var int: y ) = 
let {
  var float: value; 
  constraint binary_fuzzy_rational(values, denominator, x, y, value);
} in ( value );

predicate binary_fuzzy_rational(array[int] of int: values, int: denominator, var int: x, var int: y, var float: value ) = 
let {
  int: vals = card(dom(x)) * card(dom(y));
  array[1..vals] of 1..vals: idMap = [i | i in 1..vals];
  set of int: TABLE_INDEX = index_set(values);
  var TABLE_INDEX: chosenRow;   
 } in 
(
  encode_cost_function_binary(x,y,idMap, chosenRow) /\ values[chosenRow]/denominator = value
);

function var float: fbinary_fuzzy(array[int] of float: values, var int: x, var int: y ) = 
let {
  var float: value; 
  constraint binary_fuzzy(values, x, y, value);
} in ( value );

predicate binary_fuzzy(array[int] of float: values, var int: x, var int: y, var float: value ) = 
let {
  int: vals = card(dom(x)) * card(dom(y));
  array[1..vals] of 1..vals: idMap = [i | i in 1..vals];
  set of int: TABLE_INDEX = index_set(values);
  var TABLE_INDEX: chosenRow;   
 } in 
(
  encode_cost_function_binary(x,y,idMap, chosenRow) /\ values[chosenRow] = value
);

% These functions are equivalent to the global constraint decompositions
% for cost function (networks) ("soft_constraints/cost_functions.mzn")
% But they are replicated for the purpose of *not* being overwritten
% by the solver-specific cost functions in Numberjack/Toulbar2
% TODO maybe one could encapsulate this a little nicer to avoid code duplication
predicate encode_cost_function_binary(var int: x, var int: y, array[int] of int: costs, var int: costVariable ) = 
let {
  array[int] of int: folded = [ if(i == 1) then x_ else y_ endif | x_ in dom(x), y_ in dom(y), i in 1..2];
}
in (encode_cost_function_binary_safe(folded, costs, x, y, costVariable) );

predicate encode_cost_function_binary_safe(array[int] of int: folded, array[int] of int: costs, var int: x, var int: y, var int: costVariable) = 

assert(max(index_set(folded)) == 2*max(index_set(costs)), "Dimensions of cost vector and flattened domains must agree, i.e., you should have \(max(index_set(folded)) div 2) cost entries.", 
   let {
     int: dim = 2;
     array[int] of int: tableDecomp = [ if(j <= 2) then folded[(i-1)*dim + j] else costs[i] endif | i in index_set(costs), j in 1..3 ];
   } in table([x,y,costVariable], array2d(index_set(costs), 1..3, tableDecomp)));


% =============================================
% Example usage:
% 
/*
% A single cost function mapping to reals

function var 0.0 .. 1.0 : f_1() = 
let {
  array[int] of float: satDegrees =  [0.23, 0.45, 0.8, 0.48] ;
  var FVAL: value; 
  constraint binary_fuzzy(satDegrees, x, y, value);
} in ( value );

var 0.0 .. 1.0 : overall;
constraint overall = min(scValuations); % idempotent valuation operator
*/
% ---------------------------------------------

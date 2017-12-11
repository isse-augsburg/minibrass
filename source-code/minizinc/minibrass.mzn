include "soft_constraints/pvs_gen_search.mzn";

function ann: presolve(var int: obj, bool: doMaxim, float: perc) = 
       let {
          int: bestObj = 0;
          float: boundPerc = if doMaxim then 1.0 - perc else 1.0 + perc endif;
          int: objBound = 0;
       } in
       scope (
       repeat(
           if next() then 
                 print("Presolve solution:") /\ print() /\
                 commit() /\ bestObj := sol(obj)  /\ post( if doMaxim then obj > sol(obj) else obj < sol(obj) endif)
           else break endif
       )
      ) \/ ( objBound := if doMaxim then ceil(bestObj * boundPerc) else floor(bestObj * boundPerc) endif
             /\ print("Boundary: \(bestObj) by perc \(boundPerc) leads to objBound = \(objBound) \n") /\ post(if doMaxim then obj >= bestObj else obj <= bestObj endif) ) ;


% synonym for easier usage
function ann: presolveMax(var int: obj, float: perc) = presolve(obj, true, perc);
function ann: presolveMin(var int: obj, float: perc) = presolve(obj, false, perc);



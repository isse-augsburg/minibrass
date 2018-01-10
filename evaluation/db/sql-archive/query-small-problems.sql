SELECT problem, instance, SolverName, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
-- group by instance, SPD, Objective
where searchtype = 3 and problem ="soft-queens" and instance = "soft-queens-30.dzn"
and SPD = 1 and MIF = 0 and timeout > 10000
order by problem, instance, elapsedSecs, solverId 
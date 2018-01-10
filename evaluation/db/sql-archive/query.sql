SELECT problem, instance, SolverName, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally , Solved, Searchtype, mif FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
-- group by instance, SPD, Objective
where timeout > 10000 and solved = 0
order by problem, instance, elapsedSecs, solverId 
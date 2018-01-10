SELECT instance, searchtype, SolverName, elapsedSecs, mif, propRed, objective FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
-- group by instance, SPD, Objective
where problem = "talent-scheduling"
and instance = "film103.dzn"
and timeout > 10000
and searchType = 3
order by instance, elapsedSecs, solverId 
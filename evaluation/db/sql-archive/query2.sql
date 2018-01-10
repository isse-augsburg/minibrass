SELECT instance, SolverName, elapsedSecs FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
-- group by instance, SPD, Objective
where problem = "photo"
and SPD = 1 and MIF = 1
order by instance, elapsedSecs, solverId 
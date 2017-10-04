CREATE TEMPORARY View IF NOT EXISTS  Minima as 
 SELECT jr.problem as minProblem, jr.instance as minInstance, MIN(jr.objective) as minObjective, Min(jr.elapsedSecs) as minElapsedSecs FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID INNER JOIN ProblemInformation pi on (jr.Problem = pi.Problem and jr.Instance = pi.Instance) 
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0 and SolverId != 6 and solved = 1
Group By jr.problem, jr.instance
order by jr.problem, jr.instance
-- Explicitly ignores native Gecode since there is absolutely no difference with the Weighted MiniZinc Model

Select problem, solverId, SolverName, round(AVG(elapsedSecs),2) as "ElapsedSecs", 
                                      round(MAX(elapsedSecs)-MIN(elapsedSecs),2) as "ElapsedSecsSpan",
									  round(GeomMean(RelElapsed),2) as "RelElapsed",
                                      round(AVG(Objective),2) as "Objective", 
									  round(AVG(WeightOverhead),2) as "WeightOverhead", 
									  round(SUM(Winner),0) as "Wins",
									  round(AVG(solved)*100,2) as "Solved", round(AVG(optimally)*100,2) as "Optimally" From
( SELECT jr.problem, jr.instance, SolverName, SolverId, 
       elapsedSecs, elapsedSecs/minElapsedSecs as "RelElapsed", 
       case when solved then Objective else pi.MaxObjectiveSpd end as "Objective", 
	   (case when solved then Objective else pi.MaxObjectiveSpd end) - minObjective as "WeightOverhead",
	   case when minElapsedSecs = elapsedSecs then 1 else 0 end as "Winner",
	   Optimally, cf.ID as ConfigId, solved FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID INNER JOIN ProblemInformation pi on (jr.Problem = pi.Problem and jr.Instance = pi.Instance)
INNER JOIN Minima mins ON mins.minProblem = jr.Problem AND mins.minInstance = jr.Instance
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0 and SolverId != 6
order by jr.problem, jr.instance, objective asc, elapsedSecs, solverId)
Group By problem, solverId, SolverName
Order by problem, AVG(elapsedSecs), AVG(objective)

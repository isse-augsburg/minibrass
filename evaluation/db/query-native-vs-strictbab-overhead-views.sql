DROP View IF exists PvsData; Drop View IF exists NativeData; Drop View IF exists PvsNativeSummary;

Create TEMPORARY View IF NOT EXISTS PvsData as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 1 and Solved = 1
order by problem, instance, elapsedSecs, solverId ;

Create TEMPORARY View  IF NOT EXISTS NativeData as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 3 
order by problem, instance, elapsedSecs, solverId ;

Create TEMPORARY View IF NOT EXISTS PvsNativeSummary as 
Select pd.Problem, pd.Instance, pd.SolverName, pd.elapsedSecs as "SmythElapsed", pd.Objective as "SmythObj", nd.elapsedSecs as "WeightsElapsed", nd.Objective as "WeightsObjective" ,
       pd.elapsedSecs - nd.elapsedSecs as "AbsOverhead", 
	   pd.elapsedSecs / nd.elapsedSecs as "RelOverhead"

from PvsData pd, NativeData nd
where pd.Problem = nd.Problem and 
      pd.Instance = nd.Instance and 
	  pd.SolverId = nd.SolverId
order by pd.problem, pd.instance, pd.solverName;


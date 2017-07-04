Drop View PvsData; Drop View NativeData; Drop View PvsNativeSummary;

Create View If not Exists PvsData as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 1 and Solved = 1 AND cf.PropRed = 1
order by problem, instance, elapsedSecs, solverId ;

Create View If not Exists NativeData as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0
order by problem, instance, elapsedSecs, solverId ;

Create View If not Exists PvsNativeSummary as 
Select pd.Problem, pd.Instance, pd.SolverName, pd.elapsedSecs as "Smyth-Elapsed", pd.Objective as "Smyth-Obj", nd.elapsedSecs as "Weights-Elapsed", nd.Objective as "Weights-Objective" ,
       pd.elapsedSecs - nd.elapsedSecs as "AbsOverhead", pd.elapsedSecs / nd.elapsedSecs as "RelOverhead"

from PvsData pd, NativeData nd
where pd.Problem = nd.Problem and 
      pd.Instance = nd.Instance and 
	  pd.SolverId = nd.SolverId
order by pd.problem, pd.instance, pd.solverName;

Select Problem,
 AVG(AbsOverhead), AVG(RelOverhead) 
FROM PvsNativeSummary 
Group By Problem

Drop View If Exists NativeDataComp;
Create View If not Exists NativeDataComp as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId, solved FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0
order by problem, instance, objective asc, elapsedSecs, solverId ;

Select problem, solverId, SolverName, AVG(elapsedSecs), AVG(Objective), AVG(solved) From NativeDataComp
Group By problem, solverId, SolverName
Order by problem, AVG(elapsedSecs), AVG(objective)
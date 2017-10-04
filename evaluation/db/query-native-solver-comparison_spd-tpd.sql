
Drop View NativeDataCompSpd; Drop View NativeDataCompTpd; Drop View NativeSpd; Drop View NativeTpd;

Create View If not Exists NativeDataCompSpd as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId, solved FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0
order by problem, instance, objective asc, elapsedSecs, solverId ;

Create View If not Exists NativeDataCompTpd as 
SELECT problem, instance, SolverName, SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId, solved FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and SearchType = 3 AND cf.PropRed = 0
order by problem, instance, objective asc, elapsedSecs, solverId ;

CREATE VIEW IF not Exists NativeSpd as 
SELECT problem, instance, solverName, solverId, elapsedSecs, Objective, Optimally, Solved FROM NativeDataCompSpd;

CREATE VIEW IF not Exists NativeTpd as 
SELECT problem, instance, solverName, solverId, elapsedSecs as elapsedSecsTpd, Objective as ObjectiveTpd, Optimally as OptimallyTPD, Solved as SolvedTPD FROM NativeDataCompTpd;

Select * From
NativeSpd nspd, NativeTpd ntpd
Where nspd.problem = ntpd.problem and nspd.instance = ntpd.instance and nspd.solverId = ntpd.solverId;
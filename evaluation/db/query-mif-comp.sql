 Drop View MifData; Drop View NoMifData;

Create View If not Exists MiFData as 
SELECT problem, instance, SolverName, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 1 and timeout > 10000 and PropRed = 0 
order by problem, instance, elapsedSecs, solverId ;

Create View If not Exists NoMiFData as 
SELECT problem, instance, SolverName, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and timeout > 10000 and PropRed = 0 
order by problem, instance, elapsedSecs, solverId ;

Select Avg(Diff) From (
Select md.elapsedSecs - nmd.elapsedSecs as diff 
From MifData as md, NoMifData as nmd
Where md.problem = nmd.problem and md.instance = nmd.instance and md.SolverName = nmd.SolverName 
)
;
Drop View If Exists MifData; Drop View If Exists NoMifData; Drop View If Exists MifSummary ;

Create View If not Exists MiFData as 
SELECT problem, instance, SolverName, jr.SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId, cf.SearchType FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 1 and timeout > 10000 and PropRed = 0
order by problem, instance, elapsedSecs, solverId ;

Create View If not Exists NoMiFData as 
SELECT problem, instance, SolverName, jr.SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, Optimally, cf.ID as ConfigId, cf.SearchType FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 1 and MIF = 0 and timeout > 10000 and PropRed = 0
order by problem, instance, elapsedSecs, solverId ;

--Select Avg(Diff) From (
--Select md.elapsedSecs - nmd.elapsedSecs as diff 
--From MifData as md, NoMifData as nmd
--Where md.problem = nmd.problem and md.instance = nmd.instance and md.SolverName = nmd.SolverName 
--)

Create View If not Exists MifSummary as 
Select md.Problem, md.Instance, md.SolverName, md.elapsedSecs as "MIFelapsed", nmd.elapsedSecs as "NormalElapsed", 
md.elapsedSecs - nmd.elapsedSecs as diff,
md.elapsedSecs / nmd.elapsedSecs as RelDiff,
md.elapsedSecs < nmd.elapsedSecs as "MifWins" 
--SELECT * 
From MifData as md, NoMifData as nmd
Where md.problem = nmd.problem and md.instance = nmd.instance and md.SolverId = nmd.SolverId and md.SearchType = nmd.SearchType;

SELECT SolverName, 
--AVG(MifElapsed), AVG(NormalElapsed), 
AVG(diff) as "Diff", AVG(RelDiff) as "RelDiff", AVG(MIFelapsed) / AVG(NormalElapsed) as "RelAvgsDiff", SUM(MifWins), COUNT(*), 1.0* SUM(MifWins) / COUNT(*) as "RatioMifWins" 
FROM 
MifSummary
GROUP BY SolverName 


Drop View If Exists NonDomData; Drop View If Exists DomData; Drop View If Exists DomNonDomSummary ;

Create View If not Exists NonDomData as 
SELECT problem, instance, SolverName, jr.SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, SearchType, Solved, Optimally, cf.ID as ConfigId, cf.SearchType FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 0 and MIF = 0 and timeout > 10000 and PropRed = 0 and SearchType = 2 and solved = 1
order by problem, instance, elapsedSecs, solverId ;

Create View If not Exists DomData as 
SELECT problem, instance, SolverName, jr.SolverId, round(elapsedSecs,2) as elapsedSecs, Objective, SearchType, Solved, Optimally, cf.ID as ConfigId, cf.SearchType FROM 
JobResult jr INNER JOIN Config cf ON jr.ConfigId = cf.ID 
INNER JOIN Solver sv ON jr.SolverId = sv.ID 
where SPD = 0 and MIF = 0 and timeout > 10000 and PropRed = 1 and SearchType = 1 and solved = 1
order by problem, instance, elapsedSecs, solverId ;

Create View IF NOT EXISTS DomNonDomSummary as 
Select ndd.Problem, ndd.Instance, ndd.SolverName, 
       ndd.elapsedSecs as "NonDomElapsed", ndd.Objective as "NonDomObj", 
	   dd.elapsedSecs as "DomElapsed", dd.Objective as "DomObjective" ,
       ndd.elapsedSecs - dd.elapsedSecs as "AbsOverhead", ndd.elapsedSecs / dd.elapsedSecs as "RelOverhead"

from NonDomData ndd, Domdata dd
where ndd.Problem = dd.Problem and 
      ndd.Instance = dd.Instance and 
	  ndd.SolverId = dd.SolverId
order by ndd.problem, ndd.instance, ndd.solverName;

SELECT Problem, AVG(AbsOverhead)
FROM DomNonDomSummary
GROUP BY Problem
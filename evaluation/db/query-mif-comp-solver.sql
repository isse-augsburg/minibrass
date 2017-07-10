SELECT SolverName, 
--AVG(MifElapsed), AVG(NormalElapsed), 
AVG(diff), SUM(MifWins), COUNT(*), 1.0* SUM(MifWins) / COUNT(*) as "RatioMifWins" 
FROM 
MifSummary
GROUP BY SolverName 

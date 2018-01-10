-- SELECT SolverName, 
--AVG(MifElapsed), AVG(NormalElapsed), 
-- AVG(diff), SUM(MifWins), COUNT(*), 1.0* SUM(MifWins) / COUNT(*) as "RatioMifWins" 
-- FROM 
-- MifSummary
-- GROUP BY SolverName 


SELECT MifSummary.SolverName, Problem, 
AVG(MifElapsed) as "MifElapsed", AVG((MifElapsed - Averages.MifElapsedMean) * (MifElapsed - Averages.MifElapsedMean)) as VarMif,
AVG(NormalElapsed) as "NormalElapsed",  AVG((NormalElapsed - Averages.NormalElapsedMean) * (NormalElapsed - Averages.NormalElapsedMean)) as VarNormal
FROM 
MifSummary,
( SELECT SolverName, 
AVG(MifElapsed) as "MifElapsedMean", AVG(NormalElapsed) as "NormalElapsedMean"
FROM 
MifSummary
GROUP BY SolverName, Problem 
) AS Averages 
WHERE Averages.SolverName = MifSummary.SolverName 
GROUP BY MifSummary.SolverName, MifSummary.Problem; 

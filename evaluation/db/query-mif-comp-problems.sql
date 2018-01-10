SELECT MifSummary.Problem, 
AVG(MifElapsed) as "MifElapsed", AVG((MifElapsed - Averages.MifElapsedMean) * (MifElapsed - Averages.MifElapsedMean)) as VarMif,
AVG(NormalElapsed) as "NormalElapsed",  AVG((NormalElapsed - Averages.NormalElapsedMean) * (NormalElapsed - Averages.NormalElapsedMean)) as VarNormal,
AVG(diff), GeomMean(RelDiff),
SUM(MifWins), COUNT(*), 1.0* SUM(MifWins) / COUNT(*) as "RatioMifWins" 
FROM 
MifSummary,
( SELECT Problem, 
AVG(MifElapsed) as "MifElapsedMean", AVG(NormalElapsed) as "NormalElapsedMean"
FROM 
MifSummary
GROUP BY Problem 
) AS Averages 
WHERE Averages.Problem = MifSummary.Problem 
GROUP BY MifSummary.Problem; 



-- SELECT AVG((t.row - sub.a) * (t.row - sub.a)) as var from t, 
--    (SELECT AVG(row) AS a FROM t) AS sub;
	

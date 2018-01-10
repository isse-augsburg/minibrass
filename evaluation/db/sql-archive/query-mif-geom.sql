-- needed for geometric mean
SELECT MifElapsed, MifElapsed / NormalElapsed as "MifToNorm", NormalElapsed, NormalElapsed / MifElapsed as "NormToMif" 
FROM 
MifSummary
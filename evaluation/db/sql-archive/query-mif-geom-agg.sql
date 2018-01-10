SELECT GeomMean(MifElapsed / NormalElapsed) as "MifToNormGM", GeomMean(NormalElapsed / MifElapsed) as "NormToMifGM" 
FROM 
MifSummary
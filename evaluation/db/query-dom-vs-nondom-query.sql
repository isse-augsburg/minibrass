
SELECT Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), GeomMean(RelOverhead)
FROM DomNonDomSummary
GROUP BY Problem


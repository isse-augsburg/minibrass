
SELECT Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), AVG(RelOverhead)
FROM DomNonDomSummary
GROUP BY Problem


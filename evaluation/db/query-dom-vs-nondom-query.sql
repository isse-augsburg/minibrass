
SELECT Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), GeomMean(RelOverhead),
  Wilcoxon(NonDomElapsed, DomElapsed) as "Significant"
FROM DomNonDomSummary
GROUP BY Problem


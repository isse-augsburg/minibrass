SELECT "\emph{Overall}" AS Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), GeomMean(RelOverhead),
  Wilcoxon(NonDomElapsed, DomElapsed) as "Significant"
FROM DomNonDomSummary

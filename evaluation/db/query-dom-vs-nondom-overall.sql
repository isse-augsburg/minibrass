SELECT "\emph{Overall}" AS Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), GeomMean(RelOverhead)
  FROM DomNonDomSummary

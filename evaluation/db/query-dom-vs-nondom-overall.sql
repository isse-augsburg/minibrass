SELECT "\emph{Overall}" AS Problem,
  AVG(NonDomElapsed), AVG(DomElapsed),
  AVG(AbsOverhead), AVG(RelOverhead)
  FROM DomNonDomSummary

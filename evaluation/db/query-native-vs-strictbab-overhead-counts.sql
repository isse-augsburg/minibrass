Select Problem, SolverName, 
  AVG(SmythElapsed), AVG(WeightsElapsed),
  AVG(AbsOverhead), AVG(RelOverhead),
  AVG(WeightsObjective), AVG(SmythObj),
  AVG(1-SmythWins) as "SmythLoses"
FROM PvsNativeSummary

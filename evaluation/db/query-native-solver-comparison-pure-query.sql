Select Problem, SolverName, 
  AVG(SmythElapsed), AVG(WeightsElapsed),
  AVG(AbsOverhead), AVG(RelOverhead),
  AVG(WeightsObjective), AVG(SmythObj)
FROM PvsNativeSummary
GROUP By Problem,SolverName
ORDER BY Problem,SolverName
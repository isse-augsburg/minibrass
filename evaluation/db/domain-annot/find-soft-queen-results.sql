
SELECT jr.Problem, jr.Instance, jr.SolverId, jr.Solved, jr.Optimally, jr.Objective, jr.NoSolutions, jr.ElapsedSecs,
cf.SearchType, cf.SPD, cf.MIF, cf.PropRed FROM JobResult jr, Config cf where cf.ID = jr.ConfigID and problem = "soft-queens";
-- SELECT jr.*, cf.* FROM JobResult jr, Config cf where cf.ID = jr.ConfigID and problem = "soft-queens"
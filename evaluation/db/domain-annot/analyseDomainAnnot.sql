
SELECT nd.Problem, nd.Instance, nd.SolverId, nd.Solved as "New-Solved", od.Solved as "Old-Solved", nd.Solved != od.Solved as "Different-Solved",
       nd.Optimally as "New-Optimally", od.Optimally as "Old-Optimally", nd.Optimally != od.Optimally as "Different-Optimally",
	   nd.ElapsedSecs as "New-Elapsed", od.ElapsedSecs as "Old-Elapsed"
FROM "new-data" nd, "old-data" od where
od.problem = nd.problem and
od.instance = nd.instance and
od.solverId = nd.solverId and
od.SearchType = nd.SearchType and
od.Mif = nd.Mif and
od.SPD = nd.SPD and
od.PropRed = nd.PropRed;
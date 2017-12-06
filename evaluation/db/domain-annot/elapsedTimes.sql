
SELECT 
	   nd.ElapsedSecs as "New", od.ElapsedSecs as "Old"
FROM "new-data" nd, "old-data" od where
od.problem = nd.problem and
od.instance = nd.instance and
od.solverId = nd.solverId and
od.SearchType = nd.SearchType and
od.Mif = nd.Mif and
od.SPD = nd.SPD and
od.PropRed = nd.PropRed;
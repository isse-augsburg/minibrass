Select md.elapsedSecs - nmd.elapsedSecs as diff, md.elapsedSecs - nmd.elapsedSecs < 0 as Impr 
From MifData as md, NoMifData as nmd
Where md.problem = nmd.problem and md.instance = nmd.instance and md.SolverName = nmd.SolverName ;

Select AVG(diff), AVG(Impr), COUNT(*), AVG(MdOpt), AVG(NmdOpt) From
( 
Select md.elapsedSecs - nmd.elapsedSecs as diff, md.elapsedSecs - nmd.elapsedSecs < 0 as Impr, md.Optimally as MdOpt, nmd.Optimally as NmdOpt  
From MifData as md, NoMifData as nmd
Where md.problem = nmd.problem and md.instance = nmd.instance and md.SolverName = nmd.SolverName
);

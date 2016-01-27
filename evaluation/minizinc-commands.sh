#!/bin/sh

# G12 fd
mzn-g12fd soft-queens-weighted.mzn

# GECODE
mzn-gecode soft-queens-weighted.mzn

# Numberjack (toulbar2) 
mzn_numberjack soft-queens-weighted.mzn

# OR-Tools
minizinc -G or-tools -f fz-ort soft-queens-weighted.mzn

# JaCoP
mzn2fzn -G jacop soft-queens-weighted.mzn 
fzn-jacop soft-queens-weighted.fzn

# Now for minisearch 

minisearch --solver fzn-gecode soft-queens.mzn soft-queens.dzn
# not working due to set_in_reif ... missing in OR-tools
minisearch --solver fzn-ort soft-queens.mzn soft-queens.dzn 
# this one shows OR tools working with MiniSearch
minisearch --solver fzn-ort golomb_mybab.mzn
# it works in JaCoP as well
minisearch --solver fzn-jacop soft-queens.mzn soft-queens.dzn

minisearch --solver flatzinc -Glinear smallMIP.mzn smallMIP.dzn

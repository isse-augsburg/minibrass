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
java -cp $JAC_CP org.jacop.fz.Fz2jacop soft-queens-weighted.fzn

#!/bin/bash

# first the minisearch / spd team
minisearch soft-queens-ms.mzn soft-queens-cr.dzn 

# then the numberjack / toulbar2 team
mzn_numberjack soft-queens-nj.mzn soft-queens-cr.dzn


# Concluding, we shall have a structure for every problem P

# P-ms.mzn  ... containing the minisearch-version with either SPD/TPD, BaB or LNS
# P-nj.mzn  ... containing the numberjack-version (include soft-constraints-nj.mzn, min penSum)
# P-cr.dzn  ... containing the *same* constraint relationship for both algorithms
# P-dat.dzn ... optional additional data files (e.g. larger n values)

include "soft_constraints/cr_types.mzn";
include "soft_constraints/cr_weighting.mzn";

% ground penalties using the appropriate weighting
penalties = [weighting(s, SOFTCONSTRAINTS, crEdges, false) | s in SOFTCONSTRAINTS];
 

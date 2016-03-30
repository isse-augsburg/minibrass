include "soft_constraints/weighting_spd.mzn";
include "soft_constraints/pvs_weighted.mzn";
% aggregate weights using sum
include "soft_constraints/sum_aggregator.mzn";

% use PVS-based BaB or LNS
include "soft_constraints/pvs_search.mzn";


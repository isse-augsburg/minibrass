include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
search lns_pvs(violatedScs, lnsVars, 10, 0.5) /\ print();

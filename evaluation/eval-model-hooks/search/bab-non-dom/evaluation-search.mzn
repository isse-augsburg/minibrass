include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
search onlyNotDominatedBAB(violatedScs) /\ print();

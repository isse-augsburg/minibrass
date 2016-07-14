include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
search time_limit(timeLimitMs,  onlyNotDominatedBAB(violatedScs)) /\ print(); 

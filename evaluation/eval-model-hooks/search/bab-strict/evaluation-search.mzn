include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
search time_limit(timeLimitMs, strictlyBetterBAB(violatedScs)) /\ print(); 

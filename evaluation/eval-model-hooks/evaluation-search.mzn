include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
search strictlyBetterBAB(violatedScs) /\ print();
%include "soft_constraints/pvs_search.mzn";

solve 
:: finalAnn
minimize penSum; %search strictlyBetterBAB(violatedScs) /\ print();
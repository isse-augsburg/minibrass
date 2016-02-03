#!/bin/bash
# exports all relevant MZN files into a dedicated directory
# call with argument to specify dir name
#   ./deploy.sh SOFT-CONSTRAINT-DIRECTORY
# -------------------------------------------

EXP_LIB="soft_constraints"

if [ $# -ge 1 ] 
then
  EXP_LIB=$1
fi

echo $EXP_LIB

rm -r "$EXP_LIB"
mkdir "$EXP_LIB"

# enter all files of the utility library here 

cp constraint_relationship_search.mzn "$EXP_LIB"
cp cr_consistency.mzn "$EXP_LIB"
cp cr_types.mzn "$EXP_LIB"
cp cr_weighting.mzn "$EXP_LIB"
cp lessthan.mzn "$EXP_LIB"
cp pvs_spd.mzn "$EXP_LIB"
cp pvs_tpd.mzn "$EXP_LIB"
cp pvs_weighted.mzn "$EXP_LIB"
cp soft_constraints.mzn "$EXP_LIB"
cp spd_worse.mzn "$EXP_LIB"
cp sum_aggregator.mzn "$EXP_LIB"
cp tpd_worse.mzn "$EXP_LIB"
cp minizinc_bundle.mzn "$EXP_LIB"
cp minisearch_bundle.mzn "$EXP_LIB"
cp soft_constraints_noset.mzn "$EXP_LIB"

# global constraints 
cp globals/soft_alldifferent.mzn "$EXP_LIB"
cp globals/soft_all_different.mzn "$EXP_LIB"
cp globals/soft_all_different_int.mzn "$EXP_LIB"

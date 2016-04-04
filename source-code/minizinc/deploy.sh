#!/bin/bash
# exports all relevant MZN files into a dedicated directory
# call with argument to specify dir name
#   ./deploy.sh SOFT-CONSTRAINT-DIRECTORY
# For instance you could pass 
# your MiniZinc-std dir directly
# /> ./deploy.sh "/home/alexander/Documents/minisearch/share/minizinc/std/soft_constraints"
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

cp pvs_search.mzn "$EXP_LIB"
cp cr_consistency.mzn "$EXP_LIB"
cp cr_types.mzn "$EXP_LIB"
cp cr_weighting.mzn "$EXP_LIB"
cp pvs_spd.mzn "$EXP_LIB"
cp pvs_tpd.mzn "$EXP_LIB"
cp pvs_spd_red.mzn "$EXP_LIB"
cp pvs_tpd_red.mzn "$EXP_LIB"
cp weighting_spd.mzn "$EXP_LIB"
cp weighting_tpd.mzn "$EXP_LIB"
cp pvs_weighted.mzn "$EXP_LIB"
cp soft_constraints.mzn "$EXP_LIB"
cp spd_worse.mzn "$EXP_LIB"
cp sum_aggregator.mzn "$EXP_LIB"
cp tpd_worse.mzn "$EXP_LIB"
cp minizinc_bundle.mzn "$EXP_LIB"
cp minisearch_bundle.mzn "$EXP_LIB"
cp pvs_set_based.mzn "$EXP_LIB"

# global constraints 
cp globals/soft_alldifferent.mzn "$EXP_LIB"
cp globals/soft_all_different.mzn "$EXP_LIB"
cp globals/soft_all_different_int.mzn "$EXP_LIB"
cp globals/cost_functions.mzn "$EXP_LIB"

#!/bin/bash
# exports all relevant MZN files into a dedicated directory
# call with argument to specify dir name
#   ./deploy.sh SOFT-CONSTRAINT-DIRECTORY
# -------------------------------------------

EXP_LIB="soft-constraints"

if [ $# -ge 1 ] 
then
  EXP_LIB=$1
fi

echo $EXP_LIB

rm -r "$EXP_LIB"
mkdir "$EXP_LIB"

# enter all files of the utility library here 

cp constraint_relationship_search.mzn "$EXP_LIB"
cp cr-consistency.mzn "$EXP_LIB"
cp cr-types.mzn "$EXP_LIB"
cp cr-weighting.mzn "$EXP_LIB"
cp lessthan.mzn "$EXP_LIB"
cp pvs-spd.mzn "$EXP_LIB"
cp pvs-tpd.mzn "$EXP_LIB"
cp pvs-weighted.mzn "$EXP_LIB"
cp soft_constraints.mzn "$EXP_LIB"
cp spd_worse.mzn "$EXP_LIB"
cp sum-aggregator.mzn "$EXP_LIB"
cp tpd_worse.mzn "$EXP_LIB"
cp minizinc-bundle.mzn "$EXP_LIB"
cp minisearch-bundle.mzn "$EXP_LIB"


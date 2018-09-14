@echo off
REM exports all relevant MZN files into a dedicated directory
REM call with argument to specify dir name
REM   ./deploy.sh SOFT-CONSTRAINT-DIRECTORY
REM For instance you could pass 
REM your MiniZinc-std dir directly
REM /> ./deploy.sh "/home/alexander/Documents/minisearch/share/minizinc/std/soft_constraints"
REM -------------------------------------------
setlocal enabledelayedexpansion
set EXP_LIB=soft_constraints
@echo EXP_LIB: %EXP_LIB%

if [%1]==[] goto usage
@echo "Oh, hier will ich aber ned sein"
set EXP_LIB=%1

:usage
@echo EXP_LIB: %EXP_LIB%
@echo "I am fine"
@echo EXP_LIB: %EXP_LIB%

RMDIR /S /Q %EXP_LIB%
MKDIR %EXP_LIB%

xcopy /s mbr_types %EXP_LIB%
copy pvs_search.mzn %EXP_LIB%
copy pvs_gen_search.mzn %EXP_LIB%
copy minibrass.mzn %EXP_LIB%
copy cr_types.mzn %EXP_LIB%
copy cr_consistency.mzn %EXP_LIB%
copy cr_weighting.mzn %EXP_LIB%
copy pvs_spd.mzn %EXP_LIB%
copy pvs_tpd.mzn %EXP_LIB%
copy pvs_spd_red.mzn %EXP_LIB%
copy pvs_tpd_red.mzn %EXP_LIB%
copy weighting_spd.mzn %EXP_LIB%
copy weighting_tpd.mzn %EXP_LIB%
copy pvs_weighted.mzn %EXP_LIB%
copy soft_constraints.mzn %EXP_LIB%
copy spd_worse.mzn %EXP_LIB%
copy sum_aggregator.mzn %EXP_LIB%
copy tpd_worse.mzn %EXP_LIB%
copy minizinc_bundle.mzn %EXP_LIB%
copy minisearch_bundle.mzn %EXP_LIB%
copy pvs_set_based.mzn %EXP_LIB%
copy fuzzy_encoding.mzn %EXP_LIB%

REM global constraints 
copy globals\soft_alldifferent.mzn %EXP_LIB%
copy globals\soft_all_different.mzn %EXP_LIB%
copy globals\soft_all_different_int.mzn %EXP_LIB%
copy globals\cost_functions.mzn %EXP_LIB%

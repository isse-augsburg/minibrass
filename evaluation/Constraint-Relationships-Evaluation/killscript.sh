#!/bin/bash
pkill -f "java -cp $JAC_CP"
killall -9 python
killall -9 mzn2fzn
killall -9 fzn
killall -9 flatzinc
killall -9 fzn-gecode

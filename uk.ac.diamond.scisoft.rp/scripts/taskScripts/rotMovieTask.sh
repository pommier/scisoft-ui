#!/bin/bash

let random=$RANDOM/35
let port=9000+$random

Xvfb :$port -ac -nolisten tcp &

export DISPLAY=:$port

/home/vgb98675/ParaView-3.14.1-Linux-64bit/bin/pvbatch $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} ${14} --use-offscreen-rendering  

pkill -f "Xvfb :$port"




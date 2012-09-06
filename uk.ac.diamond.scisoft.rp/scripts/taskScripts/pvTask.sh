#!/bin/bash

let random=$RANDOM/35
let port=9000+$random

Xvfb :$port -ac -nolisten tcp &

export DISPLAY=:$port


/home/vgb98675/ParaView-3.14.1-Linux-64bit/bin/pvbatch $@ 


pkill -f "Xvfb :$port"









#!/bin/bash

setUpXvfb () {
	let random=$RANDOM/35
	let port=7800+$random
	Xvfb :$port -ac -extension GLX -nolisten tcp &
	export DISPLAY=:$port
}

closeXvfb () {
	pkill -f "Xvfb :$port"
}

tclDir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )""/Tcl/VolumeRotate_Snapshot.tcl"

if [ $# = 21 ]; then 	
	setUpXvfb
	echo n > /tmp/n
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} ${14} ${15} ${16} ${17} ${18} ${19} ${20} ${21}" < /tmp/n
	closeXvfb
elif [ $# = 18 ]; then	
	setUpXvfb
	echo n > /tmp/n
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run2 $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} ${14} ${15} ${16} ${17} ${18}" < /tmp/n
	closeXvfb
else
	echo "Invalid number of parameters passed."
fi





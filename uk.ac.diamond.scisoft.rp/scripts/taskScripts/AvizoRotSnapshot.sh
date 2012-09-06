#!/bin/bash

echo n > /tmp/n

tclDir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )""/Tcl/VolumeRotate_Snapshot.tcl"

if [ $# = 21 ]; then 	
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} ${14} ${15} ${16} ${17} ${18} ${19} ${20} ${21}" -no_gui < /tmp/n
elif [ $# = 18 ]; then	
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run2 $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11} ${12} ${13} ${14} ${15} ${16} ${17} ${18}" -no_gui < /tmp/n
else
	echo "Invalid number of parameters passed."
fi





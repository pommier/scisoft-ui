#!/bin/bash


tclDir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )""/Tcl/OrthoSlice_Snapshot.tcl"

if [ $# = 11 ]; then 
	echo n > /tmp/n
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10} ${11}" -no_gui < /tmp/n
elif [ $# = 10 ]; then
	echo n > /tmp/n
	/dls_sw/i12/software/avizo/64/bin/start -mt -tclcmd "load $tclDir; run2 $1 $2 $3 $4 $5 $6 $7 $8 $9 ${10}" -no_gui < /tmp/n
else
	echo "Invalid number of parameters passed."
fi





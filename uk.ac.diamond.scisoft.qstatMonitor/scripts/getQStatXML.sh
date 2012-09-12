#!/bin/bash

if [ $# = 1 ]; then 
	module load global/cluster &> /dev/null #redirects the standard output 
	$1 -xml
elif [ $# = 2 ]; then
	module load global/cluster &> /dev/null
	$1 -u "$2" -xml
else
	echo "Invalid number of paramters passed."
fi





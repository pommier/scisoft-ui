#!/bin/bash

echo n > /tmp/n

graphicsOption=$1
node=$2
shift
shift
#$1 and $2 have now been popped, what was $3 is now $1 


if [ "$graphicsOption" = "0" ]; then
	ssh $node $@
else 
	ssh -X $node $@
fi



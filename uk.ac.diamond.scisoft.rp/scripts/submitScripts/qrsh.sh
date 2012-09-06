#!/bin/bash

echo"yes"> /tmp/yes

module load global/cluster

qrsh -l tesla -P i12 < /tmp/yes

#run command string, will be a task script followed by arguments
$@





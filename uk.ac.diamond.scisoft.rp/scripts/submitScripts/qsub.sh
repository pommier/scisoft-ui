#!/bin/bash

module load global/cluster

qsub -l tesla -P i12 $@


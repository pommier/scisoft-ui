#!/bin/bash
java -cp test.jar:slf4j.jar:logback.jar:logbackcore.jar uk.ac.diamond.scisoft.analysis.SDAPlotInformer $1 $2 $3


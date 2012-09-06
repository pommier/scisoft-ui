#!/bin/bash

javaDir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )""/Jre/bin/java"
jarDir="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )""/Jars/IJVolRotSnapshotJar.jar"

if [ $# = 11 ] || [ $# = 14 ]; then 
	$javaDir -Xmx512m -jar $jarDir $@
else
	echo "Invalid number of parameters passed."
fi

#!/bin/bash

WORKDIR=$(pwd $(dirname $0))

echo $WORKDIR

rm -rf $WORKDIR/bin
mkdir $WORKDIR/bin

cd $WORKDIR/src

export SCALACL_SKIP=org/karatachi/scalacl/TestWithout
#export SCALACL_VERBOSE=1
export JAVA_OPTS="-server -Xms2g -Xmx2g -verbose:gc -XX:CompileThreshold=10 -XX:NewSize=1g -XX:MaxNewSize=1g -XX:SurvivorRatio=200000"

scalac -optimize -d ../bin org/karatachi/scalacl/*.scala

cd $WORKDIR/bin

scala org.karatachi.scalacl.Bootstrap

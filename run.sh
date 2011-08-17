#!/bin/bash

WORKDIR=$(pwd $(dirname $0))

echo $WORKDIR

rm -rf $WORKDIR/bin
mkdir $WORKDIR/bin

cd $WORKDIR/src

export SCALACL_SKIP=org/karatachi/scalacl/TestWithout
export SCALACL_VERBOSE=1
export JAVA_OPTS="-server -Xms1g -Xmx1g"

scalac -optimize -d ../bin org/karatachi/scalacl/*.scala

cd $WORKDIR/bin

scala org.karatachi.scalacl.Bootstrap

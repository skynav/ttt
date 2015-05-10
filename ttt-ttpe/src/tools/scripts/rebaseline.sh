#!/bin/bash
cd ../..
SRCDIR=src/test/resources/com/skynav/ttpe/app
RUNDIR=run/test
for f in $SRCDIR/test-*.xml
do
  echo Rebaselining $f ...
  cp $f $RUNDIR/test.xml
  mvn exec:java
  cp $RUNDIR/out/test.zip ${f%.xml}.expected.zip
done

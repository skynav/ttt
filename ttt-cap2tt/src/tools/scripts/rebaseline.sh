#!/bin/bash
cd ../..
SRCDIR=src/test/resources/com/skynav/cap2tt/app
RUNDIR=run/test
if [ $# -gt 0 ]; then
  p=$1
else
  p=test-
fi
for f in $SRCDIR/$p*.cap
do
  echo Rebaselining $f ...
  cp $f $RUNDIR/test.cap
  mvn exec:java
  cp $RUNDIR/test.xml ${f%.cap}.expected.xml
done

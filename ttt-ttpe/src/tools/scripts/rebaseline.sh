#!/bin/bash
cd ../..
SRCDIR=src/test/resources/com/skynav/ttpe/app
RUNDIR=run/test
if [ $# -gt 0 ]; then
  p=$1
else
  p=test-
fi
for f in $SRCDIR/$p*.xml
do
  if [ ! -e $f ]; then
    continue
  elif [ "$f" == 'test.config.xml' ]; then
    continue
  else
    echo Rebaselining $f ...
    cp $f $RUNDIR/test.xml
    mvn exec:java
    cp $RUNDIR/out/test.zip ${f%.xml}.expected.zip
  fi
done

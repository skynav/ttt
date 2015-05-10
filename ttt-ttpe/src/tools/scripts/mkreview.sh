#!/bin/bash
TOPDIR=../..
SRCDIR=$TOPDIR/src/test/resources/com/skynav/ttpe/app
RUNDIR=$TOPDIR/run/test
OUTDIR=$RUNDIR/review
OUTFIL=$OUTDIR/files.js
# create empty output review directory
[ -e $OUTDIR ] && rm -rf $OUTDIR
mkdir $OUTDIR
# populate review directory with expected test results
for f in $SRCDIR/test-*.expected.zip
do
  echo Unpacking $f for review ...
  b=$(basename "$f" .expected.zip)
  unzip -d $OUTDIR/$b $f
done
# create list of review files
[ -e $OUTFIL ] && rm $OUTFIL
touch $OUTFIL
echo "var files = [" >> $OUTFIL
for f in $(find $OUTDIR -name \*.svg -print)
do
  echo \"${f#$OUTDIR/}\", >> $OUTFIL
done
echo "\"\"" >> $OUTFIL
echo "];" >> $OUTFIL
# copy review html driver
cp $RUNDIR/review.html $OUTDIR
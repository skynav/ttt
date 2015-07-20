#!/bin/bash

mvn gpg:sign-and-deploy-file \
  -Durl=https://oss.sonatype.org/content/repositories/snapshots/ \
  -DrepositoryId=ossrh \
  -DpomFile=pom.xml \
  -Dfile=ttt-fontbox-2.0-SNAPSHOT.jar \
  -Dsources=ttt-fontbox-2.0-SNAPSHOT-sources.jar \
  -Djavadoc=ttt-fontbox-2.0-SNAPSHOT-javadoc.jar

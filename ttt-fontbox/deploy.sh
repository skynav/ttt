#!/bin/bash

# deploy pom and jar
mvn deploy:deploy-file \
  -DpomFile=ttt-fontbox-1.0-SNAPSHOT.pom \
  -Dfile=ttt-fontbox-1.0-SNAPSHOT.jar \
  -DrepositoryId=ossrh \
  -Durl=https://oss.sonatype.org/content/repositories/snapshots

# deploy sources
mvn deploy:deploy-file \
  -DgroupId=com.skynav.ttt \
  -DartifactId=ttt-fontbox \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=java-source \
  -DgeneratePom=false \
  -Dfile=ttt-fontbox-1.0-SNAPSHOT-sources.jar \
  -Dclassifier=sources \
  -DrepositoryId=ossrh \
  -Durl=https://oss.sonatype.org/content/repositories/snapshots

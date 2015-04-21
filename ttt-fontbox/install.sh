#!/bin/bash

# install pom and jar
mvn install:install-file \
  -DpomFile=ttt-fontbox-1.0-SNAPSHOT.pom \
  -Dfile=ttt-fontbox-1.0-SNAPSHOT.jar

# install sources
mvn install:install-file \
  -DgroupId=com.skynav.ttt \
  -DartifactId=ttt-fontbox \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=java-source \
  -DgeneratePom=false \
  -Dfile=ttt-fontbox-1.0-SNAPSHOT-sources.jar \
  -Dclassifier=sources

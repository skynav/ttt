#!/bin/bash

# install pom and jar
mvn install:install-file \
  -DpomFile=ttt-batik-1.0-SNAPSHOT.pom \
  -Dfile=ttt-batik-1.0-SNAPSHOT.jar

# install sources
mvn install:install-file \
  -DgroupId=com.skynav.ttt \
  -DartifactId=ttt-batik \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=java-source \
  -DgeneratePom=false \
  -Dfile=ttt-batik-1.0-SNAPSHOT-sources.jar \
  -Dclassifier=sources

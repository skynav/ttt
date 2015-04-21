#!/bin/bash

# install pom and jar
mvn install:install-file \
  -DpomFile=ttt-xmlgraphics-commons-1.0-SNAPSHOT.pom \
  -Dfile=ttt-xmlgraphics-commons-1.0-SNAPSHOT.jar

# install sources
mvn install:install-file \
  -DgroupId=com.skynav.ttt \
  -DartifactId=ttt-xmlgraphics-commons \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=java-source \
  -DgeneratePom=false \
  -Dfile=ttt-xmlgraphics-commons-1.0-SNAPSHOT-sources.jar \
  -Dclassifier=sources

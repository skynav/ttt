#!/bin/bash

# deploy pom and jars
mvn deploy:deploy-file \
  -DpomFile=ttt-xmlunit-core-2.0.0-SNAPSHOT.pom \
  -Dfile=ttt-xmlunit-core-2.0.0-SNAPSHOT.jar \
  -DrepositoryId=ossrh \
  -Durl=https://oss.sonatype.org/content/repositories/snapshots

# deploy sources
mvn deploy:deploy-file \
  -DgroupId=com.skynav.ttt \
  -DartifactId=ttt-xmlunit-core \
  -Dversion=2.0.0-SNAPSHOT \
  -Dpackaging=java-source \
  -DgeneratePom=false \
  -Dfile=ttt-xmlunit-core-2.0.0-SNAPSHOT-sources.jar \
  -Dclassifier=sources \
  -DrepositoryId=ossrh \
  -Durl=https://oss.sonatype.org/content/repositories/snapshots

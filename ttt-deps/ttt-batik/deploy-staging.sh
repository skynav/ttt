#!/bin/bash

mvn gpg:sign-and-deploy-file \
  -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
  -DrepositoryId=ossrh \
  -DpomFile=pom.xml \
  -Dfile=ttt-batik-1.0.jar \
  -Dsources=ttt-batik-1.0-sources.jar \
  -Djavadoc=ttt-batik-1.0-javadoc.jar

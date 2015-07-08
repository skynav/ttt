#!/bin/bash

mvn gpg:sign-and-deploy-file \
  -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
  -DrepositoryId=ossrh \
  -DpomFile=pom.xml \
  -Dfile=ttt-xmlunit-core-1.0.jar \
  -Dsources=ttt-xmlunit-core-1.0-sources.jar \
  -Djavadoc=ttt-xmlunit-core-1.0-javadoc.jar

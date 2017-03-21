#!/bin/sh

ENV=daily

base_dir=`pwd`

echo "start install publish..."
mvn clean install -Dmaven.test.skip=true -P $ENV

MAVEN_OPTS="-Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"

export MAVEN_OPTS

mvn -Djetty.http.port=9099 jetty:run-war

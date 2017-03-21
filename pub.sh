#!/bin/sh

ENV=online

base_dir=`pwd`
git pull

echo "start install publish..."
mvn clean install -Dmaven.test.skip=true -Ponline -f /home/shinemo-safe/localgit/publish/publish/ -U
#mvn clean install -Dmaven.test.skip=true -Pdaily -f /home/shinemo-safe/localgit/publish/publish/ -U

rm -rf /opt/shinemo/tomcat/webapps/publish*

ps -ef | grep 'tomcat' | grep -v grep| awk '{print $2}' | xargs kill -9

cp /home/shinemo-safe/localgit/publish/publish/target/publish.war /opt/shinemo/tomcat/webapps/

 /opt/shinemo/tomcat/bin/startup.sh 




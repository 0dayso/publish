mvn clean install -Dmaven.test.skip=true -Ddeploy.env=daily
-rm -rf /home/shinemo/apache-tomcat-7.0.62/webapps/medical-api.war	 
-cp /home/shinemo/gitlab/medical-api/target/medical-api.war /home/shinemo/apache-tomcat-7.0.62/webapps/



call mvn clean install -Dmaven.test.skip=true -Ddeploy.env=daily
call mvn jetty:run

pause

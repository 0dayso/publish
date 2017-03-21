#!/bin/sh
##################################################################################
##################################################################################
###build.sh appname source_path build_log_path daily/pre/online warname
##################################################################################
##################################################################################
##################################################################################
##################################################################################
DIR=`dirname $0`
PROG=`basename $0`
. $DIR/functions.sh
appname=$1
#SOURCE_PATH=/home/shinemo-safe/localgit
#SOURCE_PATH=$2
ERRORLOG=$ERRERLOG'-build-$appname.log'


if [ $# -ne 5 ]; then
	echo -e "build.sh parameters error:   "
	echo -e "build.sh appname source_path build_log_path daily|pre|online warname"
	exit 10;
fi

echo -e "start build $appname war ..."

cd $2/$1

if [ "$4" = "online" ]; then
	display_message "build online war "
#	rm $5
	mvn clean install -Dmaven.test.skip=true -Ponline > $3
	display_title "-----------------------    $appname build finished!  ------------------"
		display_message "build $1 success!"
elif [ "$4" = "pre" ]; then
	display_message "build pre war "
	mvn clean install -Dmaven.test.skip=true -Ppre > $3
	display_title "-----------------------    $appname build finished!  ------------------"
                display_message "build $1 success!"
	exit
else
	echo -e "param error:   "
	echo -e "e.g.  build.sh \$1 appname \$2 online|pre"
fi

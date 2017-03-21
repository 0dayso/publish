#!/bin/sh
##################################################################################
##################################################################################
###git.sh appname source_path giturl
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


if [ $# -ne 3 ]; then
	echo -e "git.sh parameters error:   "
	echo -e "git.sh appname source_path giturl"
	exit;
fi

echo -e "$1 init"

cd $2
git clone $3
echo -e "git clone $3  ok"

display_message "init success"


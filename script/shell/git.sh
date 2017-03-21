#!/bin/sh
##################################################################################
##################################################################################
###git.sh appname source_path branch
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
	echo -e "git.sh appname source_path branch"
	exit;
fi

echo -e "git init"

cd $2/$1
echo -e "ok"
git pull

git checkout $3


display_message "git success"


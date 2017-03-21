#!/bin/sh
##################################################################################
##################################################################################
###backup.sh appname backwar target_path remoteUser host port 
##################################################################################
##################################################################################
##################################################################################
##################################################################################
DIR=`dirname $0`
PROG=`basename $0`
. $DIR/functions.sh
appname=$1
#SOURCE_PATH=/home/shinemo-safe/localgit

if [ $# -ne 6 ]; then
	echo -e "params error: backup.sh appname backwar target_path remoteUser host port "
	exit 10;
fi

echo -e "start sync file $1 from $4 $5 $6 ..."


rsync -v -r -l -H -p -g -t -S -e  "ssh -p $6" --delete $4@$5:$3 $2
if [ $? = 0 ]
then
      display_message "sync $2 success"
      display_success
else
      display_failure
      exit 10
fi
display_message "backup success"
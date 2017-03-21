#!/bin/sh
##################################################################################
##################################################################################
###execremote.sh appname remoteUser host port cmd
##################################################################################
##################################################################################
##################################################################################
##################################################################################
DIR=`dirname $0`
PROG=`basename $0`
. $DIR/functions.sh

arr=($*)
len=$#
for((i=4;i<=${len}+1;i++));
        do cmd="$cmd ${arr[i]}";
done

#if [ $# -ne 5 ]; then
#       echo -e "execremote.sh parameters error:   $5*"
#       echo -e "execremote.sh appname remoteUser host port cmd "
#       exit 10
#fi

echo -e "start exec remote cmd  $1 $2 $3 $4 $cmd ..."

ssh $2@$3 -p $4 "$cmd"
if [ $? = 0 ]
then
      display_message "exec success"
      display_success
else
      display_failure
      exit 10
fi
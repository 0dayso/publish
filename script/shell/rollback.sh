##################################################################################
###backup.sh appname backwar target_path remote_user host port 
##################################################################################
##################################################################################
##################################################################################
##################################################################################
DIR=`dirname $0`
PROG=`basename $0`
. $DIR/functions.sh
appname=$1
#SOURCE_PATH=/home/shinemo-safe/localgit

if [ $# -ne 5 ]; then
        echo -e "params error: backup.sh appname backwar target_path remote_user host port "
        exit 10;
fi

echo -e "start sync file $1 from $4 $5 $6 ..."

localTime=$(stat -c "%Y" $2)
echo -e $localTime
remoteTime=$(ssh $4@$5 -p $6 "stat -c \"%Y\" $3 ")
echo -e $remoteTime

if [ "$localTime" = "$remoteTime" ]
        then
        echo -e "war is same to remote,ignore!"
        else
        rsync -v -r -l -H -p -g -t -S -e  "ssh -p $6" --delete $2 $4@$5:$3
fi

#rsync -v -r -l -H -p -g -t -S -e "ssh -p $5" --delete $4:$3 $2
if [ $? = 0 ]
then
      display_success
else
      display_failure
      exit 10
fi
display_message "rollback success"
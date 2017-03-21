#!/bin/bash
#***************************************************************************************************************#
# ScriptName:           functions.sh                                                                            #
# Function:             functions to be used by most or all shell scripts                                       #
#***************************************************************************************************************#
# Make sure umask is sane
umask 022

# First set up a default search path.
RSYNCCMD=`which rsync`
SSHCMD="`which ssh`"
DEPOLYDATE=`date +%Y-%m-%d-%R`

DIR=`dirname $0`
ERRERLOG="$DIR/log/$DEPOLYDATE.log"
# Get a sane screen width
[ -z "${COLUMNS:-}" ] && COLUMNS=80

BOOTUP=color
RES_COL=0
RES_COL_OK=60
MOVE_TO_COL="echo -en \\033[${RES_COL}G"
MOVE_TO_COL_OK="echo -en \\033[${RES_COL_OK}G"
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_WARNING="echo -en \\033[1;33m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"

display_success() {
  $MOVE_TO_COL_OK
  echo -n "[  "
  $SETCOLOR_SUCCESS
  echo -en $"OK"
  $SETCOLOR_NORMAL
  echo -e "  ]"
  return 0
}

display_failure() {
  $MOVE_TO_COL_OK
  echo -n "["
  $SETCOLOR_FAILURE
  echo -en $"FAILED"
  $SETCOLOR_NORMAL
  echo -e "]"
  return 1
}

display_passed() {
  $MOVE_TO_COL_OK
  echo -n "["
  $SETCOLOR_WARNING
  echo -en $"PASSED"
  $SETCOLOR_NORMAL
  echo -e "]"
  return 1
}

display_warning() {
  $MOVE_TO_COL_OK
  echo -n "["
  $SETCOLOR_WARNING
  echo -en $"WARNING"
  $SETCOLOR_NORMAL
  echo -e "]"
  return 1
}

display_message() {
  echo -en "\\033[${RES_COL}G"
  $SETCOLOR_SUCCESS
  echo -e $"$1"
  $SETCOLOR_NORMAL
  return 0
}

display_title() {
  echo -en "\\033[${RES_COL}G"
  $SETCOLOR_WARNING
  echo -e $"$1"
  $SETCOLOR_NORMAL
  return 0
}
exit_root () {
    echo "root (the superuser) can't run this script."
    exit 1
}

error_log () {
    echo "Please Check Error log $ERRERLOG!"
}

#if [ `id -u` = 0 ]
#then
#    exit_root
#fi




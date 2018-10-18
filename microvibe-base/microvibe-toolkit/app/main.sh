#!/bin/bash

CURR_DIR=$PWD

SHELL_NAME=`basename "$0"`
if [ -z "$APP_HOME" ]
then
	if [ -f "$0" ];then #绝对路径
		cd `dirname $0`
		export APP_HOME=$PWD
	elif [ -f "$CURR_DIR"/$0 ];then #相对路径
		cd `dirname "$CURR_DIR/$0"`
		export APP_HOME=$PWD
	else
		export APP_HOME=$PWD
	fi
fi
echo "APP_HOME: $APP_HOME"
cd $CURR_DIR

sh $APP_HOME/bin/change.sh $@

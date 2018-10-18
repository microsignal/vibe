#!/bin/bash

# set -e

CURR_DIR=$PWD

if [ -z "$APP_HOME" ]
then
	if [ -f "$0" -a `basename "$0"` == 'change.sh' ];then #绝对路径
		cd `dirname $0`/..
		export APP_HOME=$PWD
	elif [ -f "$CURR_DIR"/$0 -a `basename "$CURR_DIR"/$0` == 'change.sh' ];then #相对路径
		cd `dirname "$CURR_DIR/$0"`/..
		export APP_HOME=$PWD
	else
		export APP_HOME=$PWD
	fi
fi
echo "APP_HOME: $APP_HOME"
cd $CURR_DIR

# set classpath
SYS_CLASSPATH=$CLASSPATH
CLASSPATH=$APP_HOME/conf:$APP_HOME/bin


# add main jars
for jarFile in $APP_HOME/*.jar
do
	CLASSPATH=$CLASSPATH:$jarFile
done

# add all dependencies
for jarFile in $APP_HOME/lib/*.jar
do
	CLASSPATH=$CLASSPATH:$jarFile
done
# for jarFile in $APP_HOME/dependency/*.jar
# do
# 	CLASSPATH=$CLASSPATH:$jarFile
# done

# add system classpath
CLASSPATH=$CLASSPATH:$SYS_CLASSPATH
# echo "CLASSPATH: $CLASSPATH"
export CLASSPATH=$CLASSPATH


# set java home
if [ -z "$JAVA_HOME" ]
then
	export JAVA_HOME=/d/devel/Java/jdk1.8.0_144
fi
export JRE_HOME=$JAVA_HOME/jre
echo "JAVA_HOME: ${JAVA_HOME}"


# set java args
JAVA_ARGS=""
JAVA_ARGS="$JAVA_ARGS"" -Xms256m -Xmx1024m"
JAVA_ARGS="$JAVA_ARGS"" -Dfile.encoding=UTF-8"

if [ "$DEBUG" = "true" ]
then
	#JAVA_ARGS="$JAVA_ARGS"" -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=12345,server=y,suspend=y -Djava.compiler=NONE"
	JAVA_ARGS="$JAVA_ARGS"" -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=12345,server=y,suspend=n -Djava.compiler=NONE"
fi

echo "CLASSPATH: $CLASSPATH"

JAVA_ARGS="$JAVA_ARGS"" -cp $CLASSPATH"
JAVA_ARGS="$JAVA_ARGS"" io.github.microvibe.util.tools.PackageChangerRunner"

# run java program
# "$JAVA_HOME/bin/java" $JAVA_ARGS $APP_HOME/conf/change.xml
"$JAVA_HOME/bin/java" $JAVA_ARGS "$@"

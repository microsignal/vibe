#!/bin/sh

EXEC_BIN=java

EXEC_OPTS=""
EXEC_OPTS="$EXEC_OPTS -Xmx1280m"
EXEC_OPTS="$EXEC_OPTS -Dfile.encoding=utf-8"

EXEC_JAR="booster-app-boot.jar"

EXEC_ARGS=""
#EXEC_ARGS="$EXEC_ARGS --spring.profiles.active=dev"

if [ $# -gt 0 ];then
	EXEC_ARGS="$EXEC_ARGS $*"
fi

if [ -z "$( echo "$EXEC_ARGS" | grep -- '--spring.profiles.active=' )" ];then
	EXEC_ARGS="$EXEC_ARGS --spring.profiles.active=dev"
fi

if [ -z "$( echo "$EXEC_ARGS" | grep -- '--redis.host=' )" ];then
	EXEC_ARGS="$EXEC_ARGS --redis.host=127.0.0.1"
fi

if [ -z "$( echo "$EXEC_ARGS" | grep -- '--server.port=' )" ];then
	EXEC_ARGS="$EXEC_ARGS --server.port=8001"
fi

# echo $EXEC_BIN $EXEC_OPTS -jar $EXEC_JAR $EXEC_ARGS
$EXEC_BIN $EXEC_OPTS -jar $EXEC_JAR $EXEC_ARGS

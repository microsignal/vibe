#!/bin/bash
limit_in_bytes=$(cat /sys/fs/cgroup/memory/memory.limit_in_bytes)
# 默认大小
heap_size=1024
# cgroup 中的默认值
if [ "$limit_in_bytes" -ne "9223372036854771712" ]
then
    limit_in_megabytes=$(expr $limit_in_bytes \/ 1048576)
    limit_size=$(expr $limit_in_megabytes - $RESERVED_MEGABYTES)
    if [ "$limit_size" -gt "0" ]
    then
        heap_size=$limit_size
    fi
fi

export JAVA_OPTS="-Xmx${heap_size}m $JAVA_OPTS"
exec catalina.sh run

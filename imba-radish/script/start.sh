#!/bin/bash
JVM_OPTS="-Xms384m \
          -Xmx384m \
          -XX:+HeapDumpOnOutOfMemoryError \
          -XX:HeapDumpPath=./heapdump.hprof \
          -Dspring.profiles.active=prod"

if [ $# -eq 0 ]; then
    echo "请传入 Jar 包文件名（示例：sh start.sh your-app.jar）"
    exit 1
fi

JAR_PATH="$1"
if [ ! -f "$JAR_PATH" ]; then
    echo "错误：Jar包 $JAR_PATH 不存在！"
    exit 1
fi

java $JVM_OPTS -jar "$JAR_PATH" > /dev/null 2>&1 &

echo "应用已后台启动"
echo "查看进程：ps -ef | grep $JAR_PATH"
echo "查看端口：ss -tulnp | grep 8081"
echo "查看日志：tail -f logs/radish.log"
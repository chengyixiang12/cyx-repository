#!/bin/bash
JVM_OPTS="-Xms256m \
          -Xmx256m \
          -Xmn64m \
          -XX:MetaspaceSize=64m \
          -XX:MaxMetaspaceSize=128m \
          -XX:SurvivorRatio=6 \
          -XX:+UseParallelGC \
          -XX:+HeapDumpOnOutOfMemoryError \
          -XX:HeapDumpPath=./heapdump.hprof \
          -XX:+UseCompressedOops \
          -XX:+UseContainerSupport \
          -XX:+DisableExplicitGC \
          -Djava.net.preferIPv4Stack=true \
          -Dspring.context.refresh.timeout=60000"

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
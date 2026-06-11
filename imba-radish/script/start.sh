#!/bin/bash

PID_FILE="app.pid"

JVM_OPTS="-Xms256m \
          -Xmx256m \
          -XX:MaxMetaspaceSize=256m \
          -XX:CompressedClassSpaceSize=64m \
          -XX:ReservedCodeCacheSize=128m \
          -XX:MaxDirectMemorySize=64m \
          -Xss256k \
          -XX:+UseG1GC \
          -XX:+HeapDumpOnOutOfMemoryError \
          -XX:HeapDumpPath=./heapdump.hprof \
          -Dspring.profiles.active=prod"

[ $# -eq 0 ] && { echo "请传入 Jar 包文件名"; exit 1; }

JAR_PATH="$1"
[ ! -f "$JAR_PATH" ] && { echo "错误：Jar包 $JAR_PATH 不存在！"; exit 1; }

JAR_NAME=$(basename "$JAR_PATH")

# 查找进程
find_pid() {
    ps -ef | grep "java.*$JAR_NAME" | grep -v grep | awk '{print $2}'
}

# 停止旧进程
PID=$(find_pid)
if [ -n "$PID" ]; then
    echo "发现已运行进程 PID: $PID，正在停止..."
    kill -15 $PID

    for i in {1..30}; do
        if ! ps -p $PID > /dev/null 2>&1; then
            echo "进程已成功停止"
            break
        fi
        sleep 1
    done

    if ps -p $PID > /dev/null 2>&1; then
        echo "强制停止..."
        kill -9 $PID
    fi
else
    echo "未发现运行中的进程"
fi

# 启动应用
echo "正在启动应用..."
nohup java $JVM_OPTS -jar "$JAR_PATH" > /dev/null 2>&1 &
NEW_PID=$!
echo $NEW_PID > "$PID_FILE"

sleep 2
if ps -p $NEW_PID > /dev/null 2>&1; then
    echo "应用启动成功 PID: $NEW_PID"
else
    echo "应用启动失败"
    exit 1
fi
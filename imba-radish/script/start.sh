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

# 获取jar包名称（不含路径）
JAR_NAME=$(basename "$JAR_PATH")

# 停止已存在的进程
echo "检查并停止已存在的 $JAR_NAME 进程..."

# 查找正在运行的进程PID
PID=$(ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}')

if [ -n "$PID" ]; then
    echo "发现已运行的进程 PID: $PID，正在停止..."
    kill -15 $PID  # 先尝试优雅关闭

    # 等待最多30秒让进程正常退出
    for i in {1..30}; do
        if ! ps -p $PID > /dev/null 2>&1; then
            echo "进程已成功停止"
            break
        fi
        sleep 1
    done

    # 如果进程还在，强制杀死
    if ps -p $PID > /dev/null 2>&1; then
        echo "进程未响应，强制停止..."
        kill -9 $PID
        sleep 1
    fi

    echo "进程已停止"
else
    echo "未发现运行中的 $JAR_NAME 进程"
fi

# 启动新进程
echo "正在启动应用..."
java $JVM_OPTS -jar "$JAR_PATH" > /dev/null 2>&1 &

# 检查启动是否成功
NEW_PID=$!
sleep 2
if ps -p $NEW_PID > /dev/null 2>&1; then
    echo "应用已后台启动，新进程 PID: $NEW_PID"
    echo "查看进程：ps -ef | grep $JAR_NAME"
    echo "查看端口：ss -tulnp | grep 8081"
    echo "查看日志：tail -f logs/radish.log"
else
    echo "应用启动失败，请检查日志"
    exit 1
fi
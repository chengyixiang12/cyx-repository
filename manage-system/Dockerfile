# 使用官方的 OpenJDK 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 维护者信息
LABEL maintainer="1574641450@qq.com"

# 将本地的 JAR 文件复制到容器中
# 假设你的 JAR 文件是 target 目录下的 app.jar，可以根据实际情况修改
COPY /home/docker-build/manage-system/manage-system-1.0-SNAPSHOT.jar /manage-system-1.0-SNAPSHOT.jar

# 设定容器启动时运行的命令
ENTRYPOINT ["java", "-jar", "-Dfile.encoding=utf-8", "/manage-system-1.0-SNAPSHOT.jar"]
CMD ["/manage-system-1.0-SNAPSHOT.jar"]

# 容器对外暴露的端口，通常是 8081
EXPOSE 8081

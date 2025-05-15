FROM eclipse-temurin:17-jre-jammy

# 安装 libfreetype6 依赖
RUN apt-get update && apt-get install -y --no-install-recommends libfreetype6 && apt-get clean && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 维护者信息
LABEL maintainer="1574641450@qq.com"

# 配置文件路径
COPY /config/application.yml /app/config/application.yml
COPY /config/application-prod.yml /app/config/application-prod.yml

# 将本地的 JAR 文件复制到容器中
COPY ./imba-radish-1.0-SNAPSHOT.jar /imba-radish-1.0-SNAPSHOT.jar

# 设定容器启动时运行的命令
ENTRYPOINT ["java", "-Dfile.encoding=utf-8", "-jar", "/imba-radish-1.0-SNAPSHOT.jar", "--spring.profiles.active=prod"]

# 容器对外暴露的端口，通常是 8081
EXPOSE 8081

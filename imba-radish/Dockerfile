# Java 21 运行时（与 pom.xml 中 maven.compiler.release=21 一致）
FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="1574641450@qq.com"

# 系统依赖：验证码字体渲染
RUN apt-get update \
    && apt-get install -y --no-install-recommends libfreetype6 fontconfig \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 配置文件（Spring Boot 会从 classpath 加载，外部化到 /app/config/ 以支持覆盖）
COPY src/main/resources/application.yml /app/config/application.yml
COPY src/main/resources/application-prod.yml /app/config/application-prod.yml

# Spring Boot fat JAR（需先在项目根目录执行 mvn clean package）
COPY target/imba-radish-1.0-SNAPSHOT.jar /app/app.jar

# JVM 参数对齐 script/start.sh
ENV JAVA_OPTS="-Xms256m \
 -Xmx256m \
 -XX:MaxMetaspaceSize=256m \
 -XX:CompressedClassSpaceSize=64m \
 -XX:ReservedCodeCacheSize=128m \
 -XX:MaxDirectMemorySize=64m \
 -Xss256k \
 -XX:+UseG1GC \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath=/app/heapdump.hprof"

ENTRYPOINT exec java $JAVA_OPTS -Dfile.encoding=utf-8 -jar /app/app.jar --spring.profiles.active=prod

EXPOSE 8081

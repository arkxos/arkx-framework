FROM registry.cn-hangzhou.aliyuncs.com/dockerhub_mirror/java:21-anolis

WORKDIR /rapidark-quartz

ARG JAR_FILE=target/rapidark-quartz.jar

COPY ${JAR_FILE} app.jar

EXPOSE 5007

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD sleep 60; java $JAVA_OPTS -jar app.jar

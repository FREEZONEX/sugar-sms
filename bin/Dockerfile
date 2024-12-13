FROM registry.supos.ai/library/openjdk:8-jdk-alpine
ADD app.jar app.jar
ENV MEM_OPTS -Xmx512m -Xms256m -Xmn128m -Xss512k -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:/tmp/gc.log -Dfile.encoding=UTF-8 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
ENTRYPOINT ["sh","-c","java $MEM_OPTS $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
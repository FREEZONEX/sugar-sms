FROM registry.supos.ai/library/openjdk:8-jdk-alpine
ADD /common-0.0.1-SNAPSHOT.jar common.jar
ADD /sms-app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh","-c","java $MEM_OPTS $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
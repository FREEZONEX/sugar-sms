FROM openjdk:8
ADD /sms-app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh","-c","java $MEM_OPTS $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
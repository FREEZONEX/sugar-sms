# 以jdk8为基础镜像
FROM openjdk:8
# 描述
LABEL description="Java 8"
# 暴露接口
EXPOSE 8999
# 将主机中的jar包添加到镜像中
ADD /sugar-sms.jar /sugar-sms.jar
# 运行jar包
ENTRYPOINT ["java", "-Xmx1g", "-jar","sugar-sms.jar"]


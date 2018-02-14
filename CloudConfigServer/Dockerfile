FROM openjdk:9-jdk-slim
ARG JAR_FILE
ADD /target/*.jar "/app.jar"
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
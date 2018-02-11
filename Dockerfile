FROM openjdk:8-jdk-alpine
ARG JAR_FILE
ADD /target/*.jar "/app.jar"
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
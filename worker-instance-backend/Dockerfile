FROM openjdk:9-jdk-slim
ADD /target/*.jar "/app.jar"
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
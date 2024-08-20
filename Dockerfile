FROM eclipse-temurin:17-jdk-alpine

EXPOSE 8050
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} app.jar
CMD ["java","-jar","/app.jar"]

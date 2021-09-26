FROM openjdk:8-jdk-alpine
MAINTAINER Kaviarasu Sakthivadivel

#WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
COPY . /app

ENTRYPOINT ["java", "-jar", "/application.jar"]
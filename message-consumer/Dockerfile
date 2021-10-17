FROM openjdk:8-jdk-alpine
MAINTAINER Kaviarasu Sakthivadivel

#WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} message-consumer.jar
COPY . /app

ENTRYPOINT ["java", "-jar", "/message-consumer.jar"]